package Server;

import model.CountryList;
import model.MyLinkedList;
import model.MyNode;
import model.MySynchronizedQueue;
import threads.Consumer;
import threads.Producer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private ExecutorService threadPool;
    private AtomicInteger clientsFinished;
    private MySynchronizedQueue queue;
    private MyLinkedList list;
    private static CountryList listCountry;
    private int pw;
    private int deltaTime;
    private static CompletableFuture<Void> rankingFuture = null;
    private static long lastRankingCalculationTime = 0;

    public Server(int pr, int pw, int deltaTime) {
        threadPool = Executors.newFixedThreadPool(pr);
        this.pw = pw;
        this.deltaTime = deltaTime;
        clientsFinished = new AtomicInteger();
    }

    private void countryRanking() {
        synchronized (list) {
            MyNode currentMyNode = list.getHeadElement();
            while (currentMyNode != null) {
                listCountry.append(currentMyNode.score, currentMyNode.country);
                currentMyNode = currentMyNode.next;
            }
        }
        listCountry.recalibrateList();
    }

    public CompletableFuture<CountryList> handleCountryRankingsRequest() {
        long currentTime = System.currentTimeMillis();
        if (rankingFuture != null && currentTime - lastRankingCalculationTime < deltaTime) {
            System.out.println("gata rank");
            return rankingFuture.thenApply(ignored -> listCountry);
        } else {
            rankingFuture = CompletableFuture.runAsync(this::countryRanking);
            System.out.println("gata rank");
            return rankingFuture.thenApply(ignored -> listCountry);
        }
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        queue = new MySynchronizedQueue();
        list = new MyLinkedList();

        Thread[] consumersThreads = new Thread[pw];
        for (int i = 0; i < pw; i++) {
            Consumer consumer = new Consumer(queue, list, clientsFinished);
            consumersThreads[i] = new Thread(consumer);
            consumersThreads[i].start();
        }

        while (clientsFinished.get() != 5) {
            clientSocket = serverSocket.accept();
            Runnable clientHandler = new Producer(this, clientSocket, queue, clientsFinished);
            threadPool.execute(clientHandler);
        }

        threadPool.shutdown();
        Arrays.stream(consumersThreads).forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        stop();
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }
}
