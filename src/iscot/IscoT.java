package iscot;

import iscot.client.ClientArguments;
import iscot.client.CorretorMensagens;
import iscot.client.Decider;
import iscot.client.Ventilator;
import iscot.client.gui.SensorControl;
import iscot.enums.Areas;

import java.awt.GridLayout;
import javax.swing.JFrame;

public final class IscoT {

    private IscoT() {
    }

    public static void main(String[] args) {
        ClientArguments config = ClientArguments.parse(args, "iscot.IscoT");
        if (config == null) {
            return;
        }

        switch (config.type()) {
            case SENSOR -> abrirSensores(config.area());
            case DECIDER -> iniciarDecisor();
            case ACTUATOR -> iniciarAtuador(config.area());
            default -> System.err.println("Tipo de dispositivo nao suportado: " + config.type());
        }
    }

    private static void abrirSensores(Areas area) {
        JFrame janela = new JFrame("Sensores em " + area);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setLayout(new GridLayout(1, 3, 10, 10));

        janela.add(new SensorControl("", "Luz - " + area, "%", 0, 100, 1, 50,
                valor -> publicar("Luz", area, valor)));
        janela.add(new SensorControl("", "Temperatura - " + area, "ºC", -10, 50, 1, 20,
                valor -> publicar("Temperatura", area, valor)));
        janela.add(new SensorControl("", "Solo - " + area, "%", 0, 100, 1, 50,
                valor -> publicar("Solo", area, valor)));

        janela.setLocation(600, 300);
        janela.pack();
        janela.setVisible(true);
    }

    private static void publicar(String sensor, Areas area, double valor) {
        // TODO (fase 5): enviar o evento para o servidor em vez de imprimir.
        System.out.println("[Publicar Evento] " + sensor + " | Area: " + area + " | Valor: " + valor);
    }

    private static void iniciarDecisor() {
        CorretorMensagens sensores = new CorretorMensagens();
        CorretorMensagens ordens = new CorretorMensagens();
        Decider decisor = new Decider(sensores, ordens, "Decisor-01");
        decisor.setName("Decisor");
        decisor.start();
    }

    private static void iniciarAtuador(Areas area) {
        Ventilator ventilador = new Ventilator(new CorretorMensagens(), area);
        ventilador.setName("Atuador-Ventilador");
        ventilador.setDaemon(true);
        ventilador.start();
    }
}
