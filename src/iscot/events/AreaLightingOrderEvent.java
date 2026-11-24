package iscot.events;

import iscot.enums.Areas;

public class AreaLightingOrderEvent extends HighLevelOrderEvent {

    public AreaLightingOrderEvent(String deviceId, Areas area, boolean actionOn) {
        super(deviceId, area, actionOn);
    }
}
