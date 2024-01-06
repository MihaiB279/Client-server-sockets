package Server;

import model.*;
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
    private BufferedReader in;
    private PrintWriter out;
    private ExecutorService threadPool;
    private AtomicInteger clientsFinished;
    private MySynchronizedQueue queue;
    private MyLinkedList list;
    private CountryList listCountry;
    private int pw;
    private int pr;
    private int deltaTime;
    private static CompletableFuture<CountryList> rankingFuture = null;
    private static long lastRankingCalculationTime = 0;

    public Server(int pr, int pw, int deltaTime) {
        this.pr = pr;
        this.pw = pw;
        this.deltaTime = deltaTime;
        clientsFinished = new AtomicInteger();
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);

        queue = new MySynchronizedQueue();
        list = new MyLinkedList();
        threadPool = Executors.newFixedThreadPool(pr);
        for (int i = 0; i < pr; i++) {
            Runnable clientHandler = new Producer(this, clientSocket, in, out, queue, list, clientsFinished);
            threadPool.execute(clientHandler);
        }
        threadPool.shutdown();

        Thread[] consumersThreads = new Thread[pw];
        for (int i = 0; i < pw; i++) {
            Consumer consumer = new Consumer(queue, list, clientsFinished);
            consumersThreads[i] = new Thread(consumer);
            consumersThreads[i].start();
        }
        Arrays.stream(consumersThreads).forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("gata");
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }

    public synchronized CompletableFuture<CountryList> handleCountryRankingsRequest() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRankingCalculationTime < deltaTime && rankingFuture != null) {
            return rankingFuture;
        }
        rankingFuture = new CountryRanking().calculate(list);
        lastRankingCalculationTime = currentTime;
        return rankingFuture;
    }
}
