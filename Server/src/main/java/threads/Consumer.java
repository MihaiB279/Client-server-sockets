package threads;

import model.MyLinkedList;
import model.MyNode;
import model.MySynchronizedQueue;


public class Consumer implements Runnable {
    private MySynchronizedQueue queue;
    private MyLinkedList list;

    public Consumer(MySynchronizedQueue queue, MyLinkedList list) {
        this.queue = queue;
        this.list = list;
    }

    @Override
    public void run() {
        while (queue.getClientsFinished() < 5 || queue.getSize() > 0) {
            //System.out.println("Consumer:" + Thread.currentThread().getName() + " " + Server.getClientsFinished());
           // System.out.println(queue.getSize());
           // System.out.println();
            MyNode head = queue.getHeadElement();
            if (head != null) {
                String id = head.id;
                int score = head.score;
                String country = head.country;
                list.append(id, score, country);
            }
        }

    }
}