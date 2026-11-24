package iscot.server;

import iscot.events.Event;

import java.util.LinkedList;
import java.util.Queue;


public class MessageQueue {
    private final Queue<Event> queue = new LinkedList<>();


    public synchronized void put(Event event) throws InterruptedException {


        int MAX_SIZE = 10;
        while (queue.size() == MAX_SIZE) {
            wait();
        }

        queue.add(event);
        System.out.println("[Mesa] Novo evento recebido: " + event.getClass().getSimpleName());


        notifyAll();
    }


    public synchronized Event take() throws InterruptedException {

        while (queue.isEmpty()) {
            wait();
        }


        Event event = queue.poll();


        notifyAll();

        return event;
    }
}
