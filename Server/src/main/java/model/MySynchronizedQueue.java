package model;


import Server.Server;

import java.util.concurrent.atomic.AtomicInteger;

public class MySynchronizedQueue {
    private MyNode head;
    private MyNode tail;
    private AtomicInteger clientsFinished;
    private int size;
    public MySynchronizedQueue() {
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.clientsFinished = new AtomicInteger();
    }

    public synchronized void incrementClientsFinished(){
        clientsFinished.incrementAndGet();
        if(clientsFinished.get() == 5){
            notifyAll();
        }
    }

    public synchronized int getClientsFinished(){
        return clientsFinished.get();
    }

    public synchronized void append(String id, int score, String country) {
        MyNode newMyNode = new MyNode(id, score, country);
        if (head == null) {
            head = newMyNode;
            tail = newMyNode;
        } else {
            tail.next = newMyNode;
            tail = newMyNode;
        }
        size++;
        notifyAll();
    }

    public synchronized MyNode getHeadElement() {
        while (isEmpty()) {
            if(clientsFinished.get() == 5){
                return null;
            }
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        MyNode returnNode = head;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        size--;
        return returnNode;
    }

    private boolean isEmpty() {
        return head == null;
    }

    public synchronized int getSize() {
        return size;
    }
}
