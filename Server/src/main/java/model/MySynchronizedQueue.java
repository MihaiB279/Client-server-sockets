package model;


public class MySynchronizedQueue {
    private MyNode head;
    private MyNode tail;
    private int size;

    public synchronized void append(String id, int score, String country) {
        System.out.println(id + " " + score + " " + country);
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
