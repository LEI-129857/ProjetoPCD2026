package iscot.events;

import iscot.enums.Areas;

public class LightSensorEvent extends SensorEvent{

    public LightSensorEvent(String deviceId, Areas area, float value) {
        super(deviceId, area, value);
    }
}
