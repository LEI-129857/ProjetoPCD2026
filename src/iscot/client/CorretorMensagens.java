package iscot.client;


import java.util.LinkedList;
import java.util.Queue;

public class CorretorMensagens {

    private final LinkedList<Object> mesa = new LinkedList<>();

    public synchronized void put(Object newData) throws InterruptedException {

        int max = 10;
        while (this.mesa.size() == max) {
            this.wait();
        }

        this.mesa.add(newData);
        System.out.println("[Mesa] Produtor colocou evento: " + newData.getClass().getSimpleName());


        this.notifyAll();
    }

    public synchronized Object get() throws InterruptedException {

        while (this.mesa.isEmpty()) {
            this.wait();
        }


        Object retrievedData = mesa.removeFirst();
        System.out.println("[Mesa] Consumidor retirou evento: " + retrievedData.getClass().getSimpleName());


        this.notifyAll();

        return retrievedData;
    }

    public synchronized int getsize() {
        return this.mesa.size();
    }
}
