package iscot.client;

import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.SensorControl;
import iscot.enums.Areas;
import iscot.events.TemperatureSensorEvent;

public class TemperatureSensor {

    public static void main(String[] args) {
        ClientArguments config = ClientArguments.parse(args, "iscot.client.TemperatureSensor");
        if (config == null) {
            return;
        }
        Areas area = config.area();

        ServerConnection connection;
        try {
            connection = ServerConnection.connect(config);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[TemperatureSensor] Falha na ligacao inicial: " + e.getMessage());
            return;
        }

        JFrame janela = new JFrame("Temperatura em " + area + " [" + connection.getDeviceId() + "]");
        janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel();
        painel.setLayout(new BorderLayout());

        SensorControl sensorControl = new SensorControl("", "Temperatura - " + area, "ºC",
                -10, 50, 1, 20,
                valor -> {
                    try {
                        connection.send(new TemperatureSensorEvent(
                                connection.getDeviceId(), area, (float) valor));
                    } catch (IOException e) {
                        System.err.println("[TemperatureSensor] Falha ao enviar evento: " + e.getMessage());
                    }
                });

        painel.add(sensorControl, BorderLayout.CENTER);
        janela.setContentPane(painel);
        janela.setLocation(600, 400);
        janela.pack();
        janela.setVisible(true);
    }
}
