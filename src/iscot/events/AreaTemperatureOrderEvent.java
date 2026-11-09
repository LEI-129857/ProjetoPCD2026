package iscot.events;

import iscot.enums.Areas;

public class AreaTemperatureOrderEvent extends HighLevelOrderEvent{
    public AreaTemperatureOrderEvent(String deviceId, Areas area) {
        super(deviceId, area);
    }
}
