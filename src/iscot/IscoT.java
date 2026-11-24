package iscot;

import iscot.client.ClientArguments;
import iscot.client.CorretorMensagens;
import iscot.client.Decider;
import iscot.client.ServerConnection;
import iscot.client.Ventilator;
import iscot.client.gui.SensorControl;
import iscot.enums.Areas;
import iscot.events.Event;
import iscot.events.GroundMoistureSensorEvent;
import iscot.events.LightSensorEvent;
import iscot.events.TemperatureSensorEvent;

import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.JFrame;

public final class IscoT {

    private IscoT() {
    }

    public static void main(String[] args) {
        ClientArguments config = ClientArguments.parse(args, "iscot.IscoT");
        if (config == null) {
            return;
        }

        ServerConnection connection;
        try {
            connection = ServerConnection.connect(config);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[IscoT] Falha na ligacao inicial ao servidor: " + e.getMessage());
            return;
        }

        switch (config.type()) {
            case SENSOR -> abrirSensores(config.area(), connection);
            case DECIDER -> iniciarDecisor(connection);
            case ACTUATOR -> iniciarAtuador(config.area(), connection);
            default -> {
                System.err.println("Tipo de dispositivo nao suportado: " + config.type());
                connection.close();
            }
        }
    }

    private static void abrirSensores(Areas area, ServerConnection connection) {
        JFrame janela = new JFrame("Sensores em " + area + " [" + connection.getDeviceId() + "]");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setLayout(new GridLayout(1, 3, 10, 10));

        janela.add(new SensorControl("", "Luz - " + area, "%", 0, 100, 1, 50,
                valor -> publicar(connection, "Luz", area, valor)));
        janela.add(new SensorControl("", "Temperatura - " + area, "ºC", -10, 50, 1, 20,
                valor -> publicar(connection, "Temperatura", area, valor)));
        janela.add(new SensorControl("", "Solo - " + area, "%", 0, 100, 1, 50,
                valor -> publicar(connection, "Solo", area, valor)));

        janela.setLocation(600, 300);
        janela.pack();
        janela.setVisible(true);
    }

    private static void publicar(ServerConnection connection, String sensor, Areas area, double valor) {
        Event evento = switch (sensor) {
            case "Luz" -> new LightSensorEvent(connection.getDeviceId(), area, (float) valor);
            case "Temperatura" -> new TemperatureSensorEvent(connection.getDeviceId(), area, (float) valor);
            case "Solo" -> new GroundMoistureSensorEvent(connection.getDeviceId(), area, (float) valor);
            default -> null;
        };
        if (evento == null) {
            System.err.println("[IscoT] Tipo de sensor desconhecido: " + sensor);
            return;
        }
        try {
            connection.send(evento);
        } catch (IOException e) {
            System.err.println("[IscoT] Falha ao enviar evento: " + e.getMessage());
        }
    }

    private static void iniciarDecisor(ServerConnection connection) {
        Decider decisor = new Decider(connection);
        decisor.setName("Decisor-" + connection.getDeviceId());
        decisor.setDaemon(true);
        decisor.start();
        System.out.println("[IscoT] Decisor ligado ao servidor com deviceId=" + connection.getDeviceId());
    }

    private static void iniciarAtuador(Areas area, ServerConnection connection) {
        Ventilator.abrir(area, connection);
        System.out.println("[IscoT] Atuador ligado ao servidor com deviceId=" + connection.getDeviceId());
    }
}
