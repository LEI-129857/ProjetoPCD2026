package iscot.client;

import java.util.Arrays;

import iscot.enums.Areas;
import iscot.enums.Type;

public final class ClientArguments {

    private final String ip;
    private final int port;
    private final Areas area;
    private final Type type;

    private ClientArguments(String ip, int port, Areas area, Type type) {
        this.ip = ip;
        this.port = port;
        this.area = area;
        this.type = type;
    }

    public static ClientArguments parse(String[] args, String usage) {
        if (args.length != 4) {
            imprimirUso(usage, "numero de argumentos invalido");
            return null;
        }

        try {
            int port = Integer.parseInt(args[1]);
            if (port < 1 || port > 65535) {
                throw new IllegalArgumentException("porto fora do intervalo: " + port);
            }
            return new ClientArguments(args[0], port, Areas.valueOf(args[2]), Type.valueOf(args[3]));
        } catch (IllegalArgumentException e) {
            imprimirUso(usage, e.getMessage());
            return null;
        }
    }

    private static void imprimirUso(String usage, String motivo) {
        System.err.println("Argumentos invalidos (" + motivo + ").");
        System.err.println("Uso: java " + usage + " <IP> <PORT> <Area> <Tipo>");
        System.err.println("  Area: " + Arrays.toString(Areas.values()));
        System.err.println("  Tipo: " + Arrays.toString(Type.values()));
    }

    public String ip() {
        return ip;
    }

    public int port() {
        return port;
    }

    public Areas area() {
        return area;
    }

    public Type type() {
        return type;
    }
}
