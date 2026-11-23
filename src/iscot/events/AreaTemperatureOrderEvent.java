package iscot.events;

import iscot.enums.AirConditioningMode;
import iscot.enums.Areas;

public class AreaTemperatureOrderEvent extends Event {
    private final AirConditioningMode mode;

    public AreaTemperatureOrderEvent(String deviceId, Areas area, AirConditioningMode mode) {
        super(deviceId, area);
        this.mode = mode;
    }

    public AirConditioningMode getMode() {
        return mode;
    }
}