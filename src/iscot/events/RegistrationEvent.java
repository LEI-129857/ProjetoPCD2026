package iscot.events;

import iscot.enums.Areas;
import iscot.enums.Type;

public class RegistrationEvent extends Event {
    private Type type;

    public RegistrationEvent(String deviceId, Areas area, Type type) {
        // 调用父类 Event 的构造函数来赋值 deviceId 和 area
        super(deviceId, area);
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
