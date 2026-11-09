package iscot.events;

import iscot.enums.Areas;

public class HighLevelOrderEvent extends Event{

    private boolean actionOn;

    public HighLevelOrderEvent(String deviceId, Areas area) {
        super(deviceId, area);
    }

    public boolean isActionOn() {
        return actionOn;
    }
}
