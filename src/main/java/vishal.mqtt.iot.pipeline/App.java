package vishal.mqtt.iot.pipeline;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vishal.mqtt.iot.pipeline.filters.BMSDataFilter;
import vishal.mqtt.iot.pipeline.filters.NullFilters;
import vishal.mqtt.iot.pipeline.map.BMSDataMapper;
import vishal.mqtt.iot.pipeline.model.DeviceBMSData;
import vishal.mqtt.iot.pipeline.process.SuddenSOCDropProcessor;

import java.util.Properties;

public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.STREAMING);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);
        env.getCheckpointConfig().setCheckpointStorage("file:///Users/g0006686/Desktop/checkpoints");
        env.enableCheckpointing(5000L);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(5000L);
        env.getCheckpointConfig().setCheckpointTimeout(2000L);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        env.getCheckpointConfig()
                .enableExternalizedCheckpoints(
                        CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        // Setting state backend with incremental checkpointing enabled
        process(env);
        env.execute("Vishal MQTT IoT Pipeline");
    }

    private static void process(StreamExecutionEnvironment env) throws Exception {
        Properties consumerProps = new Properties();
        consumerProps.setProperty("bootstrap.servers", "localhost:9092");
        consumerProps.setProperty("group.id", "iot-pipeline-poc-v1");
        Properties producerProps = new Properties();
        producerProps.setProperty("bootstrap.servers", "localhost:9092");
        producerProps.setProperty("acks", "1");

        FlinkKafkaConsumer<String> bmsDataSource = new FlinkKafkaConsumer<String>("device.bms.data.source.v1",
                new SimpleStringSchema(), consumerProps);
        SingleOutputStreamOperator bmsDataStream = env.addSource(bmsDataSource).setParallelism(1).name("bms-data-source")
                .map(new BMSDataMapper()).setParallelism(1).name("data-mapper")
                .filter(new NullFilters<DeviceBMSData>()).setParallelism(1).name("null-filter")
                .filter(new BMSDataFilter())
                .keyBy(DeviceBMSData::getDeviceId)
                .process(new SuddenSOCDropProcessor()).setParallelism(1).name("soc-drop-processor");
        bmsDataStream.addSink(new FlinkKafkaProducer<>("soc.drop.alert.sink.v1", new SimpleStringSchema(), producerProps)).setParallelism(1).name("soc-drop-alert-sink");
    }

    }
