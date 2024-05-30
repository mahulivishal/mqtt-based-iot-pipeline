package vishal.mqtt.iot.pipeline.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceBMSState {
    public String deviceId;
    public ArrayList<Float> socValues;
    public Float soc;
    public Boolean isAlertSent;
    public Long timestamp;
}
