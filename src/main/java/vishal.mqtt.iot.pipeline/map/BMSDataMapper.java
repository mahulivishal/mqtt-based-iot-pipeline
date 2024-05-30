package vishal.mqtt.iot.pipeline.map;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.MapFunction;
import vishal.mqtt.iot.pipeline.model.DeviceBMSData;

@Slf4j
public class BMSDataMapper implements MapFunction<String, DeviceBMSData> {
    private final ObjectMapper mapper = new ObjectMapper();

    public DeviceBMSData map(String event) throws Exception {
        try {
            DeviceBMSData deviceBMSData = mapper.readValue(event, DeviceBMSData.class);
            log.info("deviceBMSData: {}", deviceBMSData);
            return deviceBMSData;
        }catch (Exception e){
            log.error("deviceBMSData ERROR: {}", event);
            log.error("Error: ", e);
            return null;
        }
    }
}
