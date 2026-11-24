package iscot.events;

import java.io.Serial;
import java.io.Serializable;
import iscot.enums.Areas;

public abstract class Event implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String deviceId;
    private final Areas area;

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