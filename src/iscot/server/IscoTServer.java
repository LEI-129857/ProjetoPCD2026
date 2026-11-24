package iscot.server;

import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.AreaTemperatureOrderEvent;
import iscot.events.Event;
import iscot.events.HighLevelOrderEvent;
import iscot.events.SensorEvent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IscoTServer {
    private final int port;
    private int nextDeviceId = 1;

    private final Map<Areas, List<ClientHandler>> decidersByArea = new HashMap<>();
    private final Map<Areas, List<ClientHandler>> actuatorsByArea = new HashMap<>();

    public IscoTServer(int port) {
        this.port = port;
    }

    public synchronized String generateUniqueDeviceId() {
        return "DEV-" + (nextDeviceId++);
    }

    private Map<Areas, List<ClientHandler>> tabelaDoTipo(Type tipo) {
        if (tipo == null) {
            return null;
        }
        return switch (tipo) {
            case DECIDER -> decidersByArea;
            case ACTUATOR -> actuatorsByArea;
            default -> null;
        };
    }

    private Map<Areas, List<ClientHandler>> tabelaDoEvento(Event evento) {
        if (evento instanceof SensorEvent) {
            return decidersByArea;
        }
        if (evento instanceof HighLevelOrderEvent) {
            return actuatorsByArea;
        }
        if (evento instanceof AreaTemperatureOrderEvent) {
            return actuatorsByArea;
        }
        return null;
    }

    public synchronized void registerClient(Areas area, Type tipo, ClientHandler handler) {
        Map<Areas, List<ClientHandler>> tabela = tabelaDoTipo(tipo);
        if (tabela == null) {
            return;
        }
        tabela.computeIfAbsent(area, k -> new ArrayList<>()).add(handler);
    }

    public synchronized void unregisterClient(Areas area, Type tipo, ClientHandler handler) {
        Map<Areas, List<ClientHandler>> tabela = tabelaDoTipo(tipo);
        if (tabela == null) {
            return;
        }
        List<ClientHandler> inscritos = tabela.get(area);
        if (inscritos != null) {
            inscritos.remove(handler);
        }
    }

    public void dispatch(ClientHandler origem, Event evento) {
        if (evento == null || evento.getArea() == null) {
            return;
        }

        Map<Areas, List<ClientHandler>> tabela = tabelaDoEvento(evento);
        if (tabela == null) {
            System.out.println("[IscoTServer] Evento ignorado (sem topico): "
                    + evento.getClass().getSimpleName());
            return;
        }

        List<ClientHandler> destinos;
        synchronized (this) {
            List<ClientHandler> inscritos = tabela.get(evento.getArea());
            if (inscritos == null || inscritos.isEmpty()) {
                return;
            }
            destinos = new ArrayList<>(inscritos);
        }

        for (ClientHandler destino : destinos) {
            if (destino != origem) {
                destino.sendEvent(evento);
            }
        }
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[IscoTServer] Servidor a escutar no porto " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[IscoTServer] Nova ligacao: " + clientSocket.getRemoteSocketAddress());

                ClientHandler handler = new ClientHandler(clientSocket, this);
                handler.start();
            }
        } catch (IOException e) {
            System.err.println("[IscoTServer] Erro no servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : 8080;
        new IscoTServer(port).start();
    }
}
