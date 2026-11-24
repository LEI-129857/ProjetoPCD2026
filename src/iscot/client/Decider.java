package iscot.client;

import iscot.enums.AirConditioningMode;
import iscot.events.AreaIrrigationOrderEvent;
import iscot.events.AreaLightingOrderEvent;
import iscot.events.AreaTemperatureOrderEvent;
import iscot.events.Event;
import iscot.events.GroundMoistureSensorEvent;
import iscot.events.LightSensorEvent;
import iscot.events.TemperatureSensorEvent;

import java.io.IOException;

public class Decider extends Thread {
    private final CorretorMensagens table;
    private final CorretorMensagens saida;
    private final String deviceId;
    private final ServerConnection connection;

    public Decider(CorretorMensagens table, CorretorMensagens saida, String deviceId) {
        this.table = table;
        this.saida = saida;
        this.deviceId = deviceId;
        this.connection = null;
    }

    /**
     * Socket mode (phase 5): receives events from the server and sends
     * high-level orders back through the same connection.
     */
    public Decider(ServerConnection connection) {
        this.table = new CorretorMensagens();
        this.saida = new CorretorMensagens();
        this.deviceId = connection.getDeviceId();
        this.connection = connection;

        connection.startReceiving(event -> {
            try {
                table.put(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object event = this.table.get();

                if (event instanceof TemperatureSensorEvent e) {
                    if (e.getValue() > 30) {
                        enviarOrdem(new AreaTemperatureOrderEvent(deviceId, e.getArea(), AirConditioningMode.Frio));
                    } else if (e.getValue() < 15) {
                        enviarOrdem(new AreaTemperatureOrderEvent(deviceId, e.getArea(), AirConditioningMode.Aquecimento));
                    }
                } else if (event instanceof LightSensorEvent e) {
                    if (e.getValue() < 30) {
                        enviarOrdem(new AreaLightingOrderEvent(deviceId, e.getArea(), true));
                    }
                } else if (event instanceof GroundMoistureSensorEvent e) {
                    if (e.getValue() < 20) {
                        enviarOrdem(new AreaIrrigationOrderEvent(deviceId, e.getArea(), true));
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Consumidor (Decisor) interrompido.");
        }
    }

    private void enviarOrdem(Event ordem) throws InterruptedException {
        saida.put(ordem);
        if (connection != null) {
            try {
                connection.send(ordem);
            } catch (IOException e) {
                System.err.println("[Decider] Falha ao enviar ordem: " + e.getMessage());
            }
        }
    }
}
