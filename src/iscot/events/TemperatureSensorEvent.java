package iscot.events;

import iscot.enums.Areas;

public class TemperatureSensorEvent extends SensorEvent{
    public TemperatureSensorEvent(String deviceId, Areas area, float value) {
        super(deviceId, area, value);
    }
}
