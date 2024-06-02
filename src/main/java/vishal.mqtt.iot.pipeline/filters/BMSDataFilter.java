package vishal.mqtt.iot.pipeline.filters;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FilterFunction;
import vishal.mqtt.iot.pipeline.model.DeviceBMSData;

@Slf4j
public class BMSDataFilter implements FilterFunction<DeviceBMSData> {

    private Long bmsSocThreshold = 0L;

    private Long OutOfOrderTimeThresholdInMS = 600000L;

    @Override
    public boolean filter(DeviceBMSData deviceBMSData) throws Exception {
        return ((System.currentTimeMillis() - deviceBMSData.getTimestamp() <= OutOfOrderTimeThresholdInMS)
                && (deviceBMSData.getSoc() > bmsSocThreshold));
    }
}
