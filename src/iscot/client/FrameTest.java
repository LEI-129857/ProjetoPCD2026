package iscot.client;

import iscot.client.gui.SensorControl;
import iscot.enums.Areas;
import iscot.events.GroundMoistureSensorEvent;
import iscot.events.LightSensorEvent;
import iscot.events.TemperatureSensorEvent;

import java.awt.GridLayout;
import javax.swing.JFrame;

public class FrameTest {
    private final JFrame frame;
    private final CorretorMensagens broker;


    public static void main(String[] args) {

        CorretorMensagens sharedTable = new CorretorMensagens();


        Decider cerebro = new Decider(sharedTable);
        cerebro.setName("Decisor-Principal");
        cerebro.start();


        FrameTest window = new FrameTest(sharedTable);
        window.open();
    }

    public FrameTest(CorretorMensagens broker) {
        this.broker = broker;
        this.frame = new JFrame("Painel de Sensores (Produtores)");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.setLayout(new GridLayout(1, 3, 10, 10));
        Areas area = Areas.LivingRoom;
        Areas areaJardim = Areas.Garden;


        frame.add(new SensorControl("💡", "Luz - " + area, "%", 0, 100, 1, 50,
                v -> {
                    try { broker.put(new LightSensorEvent("Light_01", area, (float) v)); }
                    catch (InterruptedException _) { }
                }));

        frame.add(new SensorControl("🌡️", "Temp - " + area, "ºC", -10, 50, 1, 20,
                v -> {
                    try { broker.put(new TemperatureSensorEvent("Temp_01", area, (float) v)); }
                    catch (InterruptedException _) { }
                }));

        frame.add(new SensorControl("🌱", "Solo - " + areaJardim, "%", 0, 100, 1, 50,
                v -> {
                    try { broker.put(new GroundMoistureSensorEvent("Gnd_01", areaJardim, (float) v)); }
                    catch (InterruptedException _) { }
                }));

        frame.setLocation(500, 300);
        frame.pack();
    }

    public void open() {
        frame.setVisible(true);
    }

    public CorretorMensagens getBroker() {
        return broker;
    }
}