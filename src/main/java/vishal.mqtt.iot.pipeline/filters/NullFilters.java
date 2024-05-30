package vishal.mqtt.iot.pipeline.filters;

import org.apache.flink.api.common.functions.FilterFunction;

public class NullFilters<T> implements FilterFunction<T> {

    public boolean filter(T t) throws Exception {
       return t != null;
    }
}
