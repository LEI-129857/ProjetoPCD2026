package iscot.client;

import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;

import iscot.client.gui.VentilatorIcon;
import iscot.enums.Areas;
import iscot.events.AreaVentilationOrderEvent;
import iscot.events.Event;

public class Ventilator {

    /**
     * @param args IP PORT Area Tipo
     */
    public static void main(String[] args) {
        ClientArguments config = ClientArguments.parse(args, "iscot.client.Ventilator");
        if (config == null) {
            return;
        }

        ServerConnection connection;
        try {
            connection = ServerConnection.connect(config);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Ventilator] Falha na ligacao inicial: " + e.getMessage());
            return;
        }

        abrir(config.area(), connection);
    }

    public static void abrir(Areas area, ServerConnection connection) {
        JFrame janela = new JFrame("Ventilador em " + area + " [" + connection.getDeviceId() + "]");
        janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel();
        painel.setLayout(new BorderLayout());
        VentilatorIcon ventilator = new VentilatorIcon();
        painel.add(ventilator, BorderLayout.CENTER);
        janela.setContentPane(painel);
        janela.setLocation(600, 400);
        janela.pack();
        janela.setVisible(true);

        connection.startReceiving((Event event) -> {
            if (event instanceof AreaVentilationOrderEvent ordem
                    && ordem.getArea() == area) {
                ventilator.setAtivo(ordem.isActionOn());
                System.out.println("[Ventilator] Ordem recebida: ligado=" + ordem.isActionOn());
            }
        });
    }
}
