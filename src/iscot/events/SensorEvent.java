package iscot.events;
import iscot.enums.Areas;

public class SensorEvent extends Event{
    private float value;
    public SensorEvent(String deviceId, Areas area, float value) {
        // 调用基础 Event 类的构造器，传入设备 ID 和区域
        super(deviceId, area);
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}
