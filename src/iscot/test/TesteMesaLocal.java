package iscot.test;

import java.util.LinkedList;
import java.util.Queue;

public class TesteMesaLocal {

    static class MockEvent {
        String nome;

        public MockEvent(String nome) {
            this.nome = nome;
        }
    }

    static class MessageQueue {
        private final Queue<MockEvent> queue = new LinkedList<>();
        private final int MAX_SIZE = 3;

        public synchronized void put(MockEvent event) throws InterruptedException {
            while (queue.size() == MAX_SIZE) {
                System.out.println("[Mesa] Mesa cheia. Produtor a aguardar.");
                wait();
            }
            queue.add(event);
            System.out.println("[Mesa] Novo evento na mesa: " + event.nome);
            notifyAll();
        }

        public synchronized MockEvent take() throws InterruptedException {
            while (queue.isEmpty()) {
                System.out.println("[Mesa] Mesa vazia. Consumidor a aguardar.");
                wait();
            }
            MockEvent event = queue.poll();
            System.out.println("[Mesa] Evento retirado: " + event.nome);
            notifyAll();
            return event;
        }
    }

    static class ProducerThread extends Thread {
        private final MessageQueue table;

        public ProducerThread(MessageQueue table) {
            this.table = table;
        }

        @Override
        public void run() {
            try {
                int contador = 1;
                while (true) {
                    Thread.sleep(1000);
                    MockEvent evento = new MockEvent("Temperatura_" + contador++);
                    System.out.println("[Produtor] A tentar colocar evento...");
                    table.put(evento);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class ConsumerThread extends Thread {
        private final MessageQueue table;

        public ConsumerThread(MessageQueue table) {
            this.table = table;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(2500);
                    System.out.println("[Consumidor] A tentar retirar evento...");
                    table.take();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== A iniciar teste local da Mesa ===");

        MessageQueue mesa = new MessageQueue();

        ProducerThread sensor = new ProducerThread(mesa);
        ConsumerThread ventilador = new ConsumerThread(mesa);

        sensor.start();
        ventilador.start();

        System.out.println("=== Threads a correr em paralelo. Observa a consola. ===");
    }
}
