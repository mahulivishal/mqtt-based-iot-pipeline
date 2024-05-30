package vishal.mqtt.iot.pipeline.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import vishal.mqtt.iot.pipeline.model.Alert;
import vishal.mqtt.iot.pipeline.model.DeviceBMSData;
import vishal.mqtt.iot.pipeline.model.DeviceBMSState;

import java.util.ArrayList;

@Slf4j
public class SuddenSOCDropProcessor extends KeyedProcessFunction<String, DeviceBMSData, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    private transient ValueState<DeviceBMSState> deviceBMSStateValueState;

    private Long socDropDiffThreshold = 10L;

    private Long socPacketCount = 2L;

    @Override
    public void open(OpenContext openContext) throws Exception {
        ValueStateDescriptor<DeviceBMSState> deviceStateValueStateDescriptor = new ValueStateDescriptor<>("device_bms_state", DeviceBMSState.class);
        this.deviceBMSStateValueState = getRuntimeContext().getState(deviceStateValueStateDescriptor);
    }

    @Override
    public void processElement(DeviceBMSData deviceBMSData, KeyedProcessFunction<String, DeviceBMSData, String>.Context context, Collector<String> collector) throws Exception {
        DeviceBMSState deviceBMSState = deviceBMSStateValueState.value();
        if(null == deviceBMSState){
            deviceBMSState = DeviceBMSState.builder()
                    .deviceId(deviceBMSData.getDeviceId())
                    .soc(deviceBMSData.getSoc())
                    .timestamp(deviceBMSData.getTimestamp())
                    .socValues(new ArrayList<>())
                    .isAlertSent(false)
                    .build();
            deviceBMSStateValueState.update(deviceBMSState);
        }
        else {
            if(deviceBMSData.getSoc() > deviceBMSState.getSoc()){
                deviceBMSState.setSoc(deviceBMSData.getSoc());
                deviceBMSState.setSocValues(new ArrayList<>());
                deviceBMSState.setTimestamp(deviceBMSData.getTimestamp());
                deviceBMSStateValueState.update(deviceBMSState);
                return;
            }
            if(deviceBMSState.getSocValues().size() == (socPacketCount - 1)){
                ArrayList<Float> socValues = deviceBMSState.getSocValues();
                socValues.add(deviceBMSData.getSoc());
                double avg = socValues.stream()
                        .mapToDouble(d -> d)
                        .average()
                        .orElse(0L);
                if(deviceBMSState.getSoc() - avg <= socDropDiffThreshold){
                    String message = "Sudden SOC Drop of about "+ avg +"% detected! Please connect to a charger ASAP!";
                    log.info("ALERT: {}", message);
                    Alert alert = Alert.builder()
                            .deviceId(deviceBMSState.getDeviceId())
                            .message(message)
                            .build();
                    collector.collect(mapper.writeValueAsString(alert));
                    deviceBMSState.setSoc(deviceBMSData.getSoc());
                    deviceBMSState.setIsAlertSent(true);
                    deviceBMSState.setSocValues(new ArrayList<>());
                    deviceBMSState.setTimestamp(deviceBMSData.getTimestamp());
                    deviceBMSStateValueState.update(deviceBMSState);
                }
            }else{
                if(deviceBMSState.getSoc() - deviceBMSData.getSoc() >= socDropDiffThreshold){
                    ArrayList<Float> socValues = deviceBMSState.getSocValues();
                    socValues.add(deviceBMSData.getSoc());
                    deviceBMSState.setSocValues(socValues);
                    deviceBMSStateValueState.update(deviceBMSState);
                }
            }
        }
    }

}
