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
import java.util.concurrent.*;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private ExecutorService threadPool;
    private MySynchronizedQueue queue;
    private MyLinkedList list;
    private int pw;
    private int pr;
    private int deltaTime;
    private static CompletableFuture<CountryList> rankingFuture = null;
    private static long lastRankingCalculationTime = 0;
    private ExecutorService executor = Executors.newFixedThreadPool(5);

    public Server(int pr, int pw, int deltaTime) {
        this.pr = pr;
        this.pw = pw;
        this.deltaTime = deltaTime;
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);

        queue = new MySynchronizedQueue();
        list = new MyLinkedList();
        threadPool = Executors.newFixedThreadPool(pr + pw);
        for (int i = 0; i < pr; i++) {
            Runnable clientHandler = new Producer(this, in, out, queue, list);
            threadPool.execute(clientHandler);
        }
        for (int i = 0; i < pw; i++) {
            Runnable consumer = new Consumer(queue, list);
            threadPool.execute(consumer);
        }
        threadPool.shutdown();

        try {
            threadPool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() throws IOException {
        executor.shutdown();
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }

    public  Future<CountryList> handleCountryRankingsRequest() throws ExecutionException, InterruptedException {
        Future<CountryList> result = executor.submit(() -> {
            CountryList listCountry = new CountryList();
            MyNode currentMyNode = list.getHeadElement();
            while (currentMyNode != null) {
                listCountry.append(currentMyNode.score, currentMyNode.country);
                currentMyNode = currentMyNode.next;
            }
            listCountry.recalibrateList();
            return listCountry;
        });

        return result;
    }
}
