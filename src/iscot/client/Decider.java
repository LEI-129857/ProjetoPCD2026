package iscot.client;

import iscot.events.GroundMoistureSensorEvent;
import iscot.events.LightSensorEvent;
import iscot.events.TemperatureSensorEvent;

public class Decider extends Thread {
    private CorretorMensagens table;

    public Decider(CorretorMensagens table) {
        this.table = table;
    }

    @Override
    public void run() {
        try {
            while (true) {

                Object event = this.table.get();


                if (event instanceof TemperatureSensorEvent) {
                    TemperatureSensorEvent e = (TemperatureSensorEvent) event;
                    if (e.getValue() > 30) {
                        System.out.println("[DECISÃO] O Decisor ligou o AC (FRIO) na " + e.getArea());
                    } else if (e.getValue() < 15) {
                        System.out.println("[DECISÃO] O Decisor ligou o AC (QUENTE) na " + e.getArea());
                    }
                }
                else if (event instanceof LightSensorEvent) {
                    LightSensorEvent e = (LightSensorEvent) event;
                    if (e.getValue() < 30) {
                        System.out.println("[DECISÃO] O Decisor ACENDEU a Lâmpada na " + e.getArea());
                    }
                }
                else if (event instanceof GroundMoistureSensorEvent) {
                    GroundMoistureSensorEvent e = (GroundMoistureSensorEvent) event;
                    if (e.getValue() < 20) {
                        System.out.println("[DECISÃO] O Decisor LIGOU a Rega no " + e.getArea());
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Consumidor (Decisor) interrompido.");
        }
    }
}