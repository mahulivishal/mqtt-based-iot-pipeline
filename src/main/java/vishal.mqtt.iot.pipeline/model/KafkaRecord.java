package vishal.mqtt.iot.pipeline.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KafkaRecord {
    private String key;
    private String value;

    public KafkaRecord(String key, String value) {
        this.key = key;
        this.value = value;
    }

}
