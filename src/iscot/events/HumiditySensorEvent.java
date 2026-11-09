package iscot.events;

import iscot.enums.Areas;

public class HumiditySensorEvent extends SensorEvent{
    public HumiditySensorEvent(String deviceId, Areas area, float value) {
        super(deviceId, area, value);
    }
}
