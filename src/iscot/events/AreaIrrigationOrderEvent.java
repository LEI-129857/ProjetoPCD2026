package iscot.events;

import iscot.enums.Areas;

public class AreaIrrigationOrderEvent extends HighLevelOrderEvent{
    public AreaIrrigationOrderEvent(String deviceId, Areas area) {
        super(deviceId, area);
    }
}
