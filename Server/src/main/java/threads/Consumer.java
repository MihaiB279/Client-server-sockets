package threads;

import model.MyLinkedList;
import model.MyNode;
import model.MySynchronizedQueue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements Runnable {
    private MySynchronizedQueue queue;
    private MyLinkedList list;
    private AtomicInteger clientsFinished;

    public Consumer(MySynchronizedQueue queue, MyLinkedList list, AtomicInteger clientsFinished) {
        this.queue = queue;
        this.clientsFinished = clientsFinished;
        this.list = list;
    }

    @Override
    public void run() {
        while (clientsFinished.get() != 5 || queue.getSize() > 0) {
            //System.out.println(clientsFinished.get());
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