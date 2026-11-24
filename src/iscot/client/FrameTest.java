package iscot.client;

import iscot.client.gui.SensorControl;
import iscot.enums.Areas;
import iscot.events.Event;
import iscot.events.GroundMoistureSensorEvent;
import iscot.events.LightSensorEvent;
import iscot.events.TemperatureSensorEvent;

import java.awt.GridLayout;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JFrame;

public class FrameTest {
    private final JFrame frame;
    private final CorretorMensagens broker;
    private final ExecutorService publicador;

    public static void main(String[] args) {
        CorretorMensagens sharedTable = new CorretorMensagens();
        CorretorMensagens ordemTable = new CorretorMensagens();

        Decider cerebro = new Decider(sharedTable, ordemTable, "Decider-01");
        cerebro.setName("Decisor-Principal");
        cerebro.setDaemon(true);
        cerebro.start();

        FrameTest window = new FrameTest(sharedTable);
        window.open();
    }

    public FrameTest(CorretorMensagens broker) {
        this.broker = broker;

        this.publicador = Executors.newSingleThreadExecutor(tarefa -> {
            Thread t = new Thread(tarefa, "publicador-sensores");
            t.setDaemon(true);
            return t;
        });

        this.frame = new JFrame("Painel de Sensores (Produtores)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(new GridLayout(1, 3, 10, 10));
        Areas area = Areas.LivingRoom;
        Areas areaJardim = Areas.Garden;

        frame.add(new SensorControl("", "Luz - " + area, "%", 0, 100, 1, 50,
                v -> publicar(new LightSensorEvent("Light_01", area, (float) v))));

        frame.add(new SensorControl("", "Temp - " + area, "\u00baC", -10, 50, 1, 20,
                v -> publicar(new TemperatureSensorEvent("Temp_01", area, (float) v))));

        frame.add(new SensorControl("", "Solo - " + areaJardim, "%", 0, 100, 1, 50,
                v -> publicar(new GroundMoistureSensorEvent("Gnd_01", areaJardim, (float) v))));

        frame.setLocation(500, 300);
        frame.pack();
    }

    private void publicar(Event evento) {
        publicador.submit(() -> {
            try {
                broker.put(evento);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public void open() {
        frame.setVisible(true);
    }

    public CorretorMensagens getBroker() {
        return broker;
    }
}
