package iscot.events;

import iscot.enums.Areas;

public abstract class Event {
    private String deviceId;
    private Areas area;

    public Event(String deviceId, Areas area) {
        this.deviceId = deviceId;
        this.area = area;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Areas getArea() {
        return area;
    }
}