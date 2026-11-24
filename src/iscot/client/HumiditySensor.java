package iscot.client;

import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import iscot.client.gui.SensorControl;
import iscot.enums.Areas;
import iscot.events.HumiditySensorEvent;

public class HumiditySensor {

    public static void main(String[] args) {
        ClientArguments config = ClientArguments.parse(args, "iscot.client.HumiditySensor");
        if (config == null) {
            return;
        }
        Areas area = config.area();

        ServerConnection connection;
        try {
            connection = ServerConnection.connect(config);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[HumiditySensor] Falha na ligacao inicial: " + e.getMessage());
            return;
        }

        JFrame janela = new JFrame("Humidade em " + area + " [" + connection.getDeviceId() + "]");
        janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel();
        painel.setLayout(new BorderLayout());

        SensorControl sensorControl = new SensorControl("", "Humidade - " + area, "%",
                0, 100, 1, 50,
                valor -> {
                    try {
                        connection.send(new HumiditySensorEvent(
                                connection.getDeviceId(), area, (float) valor));
                    } catch (IOException e) {
                        System.err.println("[HumiditySensor] Falha ao enviar evento: " + e.getMessage());
                    }
                });

        painel.add(sensorControl, BorderLayout.CENTER);
        janela.setContentPane(painel);
        janela.setLocation(600, 400);
        janela.pack();
        janela.setVisible(true);
    }
}
