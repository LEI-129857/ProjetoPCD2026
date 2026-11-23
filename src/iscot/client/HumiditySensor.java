package iscot.client;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import iscot.client.gui.SensorControl;
import iscot.enums.Areas;

public class HumiditySensor {

    public static void main(String[] args) {
        ClientArguments config = ClientArguments.parse(args, "iscot.client.HumiditySensor");
        if (config == null) {
            return;
        }
        Areas area = config.area();

        // TODO (fase 4/5): ligar a config.ip()/config.port() e registar o dispositivo.

        JFrame janela = new JFrame("Humidade em " + area);
        janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel();
        painel.setLayout(new BorderLayout());

        SensorControl sensorControl = new SensorControl("", "Humidade - " + area, "%",
                0, 100, 1, 50,
                valor -> {
                    // TODO (fase 5): enviar o evento para o servidor em vez de imprimir.
                    System.out.println("[Publicar Evento] Area: " + area
                            + " | Humidade: " + valor + "%");
                });

        painel.add(sensorControl, BorderLayout.CENTER);
        janela.setContentPane(painel);
        janela.setLocation(600, 400);
        janela.pack();
        janela.setVisible(true);
    }
}
