package iscot.events;

import iscot.enums.Areas;

public class HighLevelOrderEvent extends Event {

    private boolean actionOn;

    public HighLevelOrderEvent(String deviceId, Areas area, boolean actionOn) {
        super(deviceId, area);
        this.actionOn = actionOn;
    }

    public boolean isActionOn() {
        return actionOn;
    }
}