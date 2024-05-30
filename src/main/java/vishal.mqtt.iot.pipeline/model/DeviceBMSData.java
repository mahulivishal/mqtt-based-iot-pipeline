package vishal.mqtt.iot.pipeline.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceBMSData {
    public String deviceId;
    public Float soc;
    public Long timestamp;
}
