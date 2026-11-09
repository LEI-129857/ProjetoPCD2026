package iscot.events;

import iscot.enums.Areas;

public class AreaVentilationOrderEvent extends HighLevelOrderEvent{
    public AreaVentilationOrderEvent(String deviceId, Areas area, boolean b) {
        super(deviceId, area);
    }
}
