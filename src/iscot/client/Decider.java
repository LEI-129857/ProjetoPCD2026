package iscot.client;

import iscot.enums.AirConditioningMode;
import iscot.events.AreaIrrigationOrderEvent;
import iscot.events.AreaLightingOrderEvent;
import iscot.events.AreaTemperatureOrderEvent;
import iscot.events.GroundMoistureSensorEvent;
import iscot.events.LightSensorEvent;
import iscot.events.TemperatureSensorEvent;

public class Decider extends Thread {
    private final CorretorMensagens table;
    private final CorretorMensagens saida;
    private final String deviceId;

    public Decider(CorretorMensagens table, CorretorMensagens saida, String deviceId) {
        this.table = table;
        this.saida = saida;
        this.deviceId = deviceId;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object event = this.table.get();

                if (event instanceof TemperatureSensorEvent) {
                    TemperatureSensorEvent e = (TemperatureSensorEvent) event;
                    if (e.getValue() > 30) {
                        saida.put(new AreaTemperatureOrderEvent(deviceId, e.getArea(), AirConditioningMode.Frio));
                    } else if (e.getValue() < 15) {
                        saida.put(new AreaTemperatureOrderEvent(deviceId, e.getArea(), AirConditioningMode.Aquecimento));
                    }
                } else if (event instanceof LightSensorEvent) {
                    LightSensorEvent e = (LightSensorEvent) event;
                    if (e.getValue() < 30) {
                        saida.put(new AreaLightingOrderEvent(deviceId, e.getArea(), true));
                    }
                } else if (event instanceof GroundMoistureSensorEvent) {
                    GroundMoistureSensorEvent e = (GroundMoistureSensorEvent) event;
                    if (e.getValue() < 20) {
                        saida.put(new AreaIrrigationOrderEvent(deviceId, e.getArea(), true));
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Consumidor (Decisor) interrompido.");
        }
    }
}