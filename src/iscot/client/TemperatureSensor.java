package iscot.client;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.SensorControl;
import iscot.enums.Areas;

public class TemperatureSensor {

    public static void main(String[] args) {
        ClientArguments config = ClientArguments.parse(args, "iscot.client.TemperatureSensor");
        if (config == null) {
            return;
        }
        Areas area = config.area();

        // TODO (fase 4/5): ligar a config.ip()/config.port() e registar o dispositivo.

        JFrame janela = new JFrame("Temperatura em " + area);
        janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel();
        painel.setLayout(new BorderLayout());

        SensorControl sensorControl = new SensorControl("", "Temperatura - " + area, "ºC",
                -10, 50, 1, 20,
                valor -> {
                    // TODO (fase 5): enviar o evento para o servidor em vez de imprimir.
                    System.out.println("[Publicar Evento] Area: " + area
                            + " | Temperatura: " + valor + "ºC");
                });

        painel.add(sensorControl, BorderLayout.CENTER);
        janela.setContentPane(painel);
        janela.setLocation(600, 400);
        janela.pack();
        janela.setVisible(true);
    }
}
