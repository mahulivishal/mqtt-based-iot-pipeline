package vishal.mqtt.iot.pipeline.filters;

import org.apache.flink.api.common.functions.FilterFunction;
import vishal.mqtt.iot.pipeline.model.DeviceBMSData;

public class BMSDataFilter implements FilterFunction<DeviceBMSData> {

    private Long bmsSocThreshold = 0L;

    private Long OutOfOrderTimeThresholdInMS = 60000L;

    @Override
    public boolean filter(DeviceBMSData deviceBMSData) throws Exception {
        return ((System.currentTimeMillis() - deviceBMSData.getTimestamp() <= OutOfOrderTimeThresholdInMS)
                && (deviceBMSData.getSoc() > bmsSocThreshold));
    }
}
