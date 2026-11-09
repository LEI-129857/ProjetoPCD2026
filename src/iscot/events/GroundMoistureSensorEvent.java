package iscot.events;

import iscot.enums.Areas;

public class GroundMoistureSensorEvent extends SensorEvent{
    public GroundMoistureSensorEvent(String deviceId, Areas area, float value) {
        super(deviceId, area, value);
    }
}
