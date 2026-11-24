package iscot.client;

import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.Event;
import iscot.events.RegistrationEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Handles client-server connection over TCP object streams:
 * registration handshake (phase 4) and event exchange (phase 5).
 */
public class ServerConnection {

    private final String ip;
    private final int port;
    private final Areas area;
    private final Type type;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String deviceId;
    private Thread receiverThread;

    public ServerConnection(String ip, int port, Areas area, Type type) {
        this.ip = ip;
        this.port = port;
        this.area = area;
        this.type = type;
    }

    public static ServerConnection connect(ClientArguments config) throws IOException, ClassNotFoundException {
        ServerConnection connection = new ServerConnection(
                config.ip(), config.port(), config.area(), config.type());
        connection.connect();
        return connection;
    }

    /**
     * Connects to the IscoT server and performs the initial registration.
     * The server replies with the unique device ID assigned to this client.
     */
    public void connect() throws IOException, ClassNotFoundException {
        socket = new Socket(ip, port);

        // Same stream order as ClientHandler: ObjectOutputStream first, then flush,
        // then ObjectInputStream, to avoid Java serialization stream-header deadlock.
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        RegistrationEvent registration = new RegistrationEvent(null, area, type);
        send(registration);

        Object response = in.readObject();
        if (!(response instanceof String assignedId)) {
            close();
            throw new IOException("Resposta de registo invalida: " + response);
        }

        this.deviceId = assignedId;
        System.out.println("[ServerConnection] Registado no servidor como " + deviceId
                + " | Tipo: " + type
                + " | Area: " + area);
    }

    public synchronized void send(Event event) throws IOException {
        ensureConnected();
        out.writeObject(event);
        out.flush();
        out.reset(); // avoid ObjectOutputStream caching serialized object graphs
        System.out.println("[ServerConnection] Evento enviado: " + event.getClass().getSimpleName());
    }

    public Object receive() throws IOException, ClassNotFoundException {
        ensureConnected();
        return in.readObject();
    }

    /**
     * Starts a background thread that listens for events coming from the server
     * and forwards them to the provided handler.
     */
    public void startReceiving(Consumer<Event> onEvent, Consumer<Exception> onError) {
        if (receiverThread != null) {
            throw new IllegalStateException("Receiver ja foi iniciado.");
        }
        receiverThread = new Thread(() -> {
            try {
                while (isConnected()) {
                    Object received = in.readObject();
                    if (received instanceof Event event) {
                        onEvent.accept(event);
                    } else {
                        System.err.println("[ServerConnection] Objeto ignorado: " + received);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                if (isConnected() && onError != null) {
                    onError.accept(e);
                }
            } finally {
                close();
            }
        }, "ServerConnection-Receiver-" + deviceId);
        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    public void startReceiving(Consumer<Event> onEvent) {
        startReceiving(onEvent, e -> System.err.println("[ServerConnection] Erro no receptor: " + e.getMessage()));
    }

    private void ensureConnected() throws IOException {
        if (socket == null || socket.isClosed() || out == null || in == null) {
            throw new IOException("Cliente nao esta ligado ao servidor.");
        }
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Areas getArea() {
        return area;
    }

    public Type getType() {
        return type;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed() && deviceId != null;
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {
            // Closing an already broken socket is not actionable here.
        }
    }
}
