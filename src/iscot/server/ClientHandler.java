package iscot.server;

import iscot.enums.Areas;
import iscot.enums.Type;
import iscot.events.Event;
import iscot.events.RegistrationEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final IscoTServer server;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private String deviceId;
    private Areas area;
    private Type tipo;

    public ClientHandler(Socket socket, IscoTServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            Object primeiraMensagem = in.readObject();

            if (!(primeiraMensagem instanceof RegistrationEvent registo)) {
                System.err.println("[Handler] Ligacao recusada: a primeira mensagem nao e um RegistrationEvent.");
                closeConnection();
                return;
            }

            if (registo.getType() == null || registo.getArea() == null) {
                System.err.println("[Handler] Ligacao recusada: tipo ou area invalidos.");
                closeConnection();
                return;
            }

            this.deviceId = server.generateUniqueDeviceId();
            this.area = registo.getArea();
            this.tipo = registo.getType();
            server.registerClient(this.area, this.tipo, this);

            System.out.println("[Handler] Cliente registado: " + deviceId
                    + " | Tipo: " + tipo
                    + " | Area: " + area);

            out.writeObject(deviceId);
            out.flush();

            while (true) {
                Object recebido = in.readObject();
                if (recebido instanceof Event evento) {
                    server.dispatch(this, evento);
                } else {
                    System.err.println("[Handler] Objeto recebido nao e um Event: " + recebido);
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[Handler] Cliente " + deviceId + " desligado.");
        } finally {
            if (this.area != null && this.tipo != null) {
                server.unregisterClient(this.area, this.tipo, this);
            }
            closeConnection();
        }
    }

    public synchronized void sendEvent(Event event) {
        try {
            out.writeObject(event);
            out.flush();
        } catch (IOException e) {
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }
}
