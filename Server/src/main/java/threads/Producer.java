package threads;

import Server.Server;
import model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class Producer implements Runnable {
    private MySynchronizedQueue queue;
    private MyLinkedList list;
    private Socket clientSocket;
    private AtomicInteger clientsFinished;
    private Server server;
    private BufferedReader in;
    private PrintWriter out;

    public Producer(Server server, Socket socket, BufferedReader in, PrintWriter out, MySynchronizedQueue queue, MyLinkedList list, AtomicInteger clientsFinished) {
        this.server = server;
        this.queue = queue;
        this.in = in;
        this.out = out;
        this.list = list;
        this.clientSocket = socket;
        this.clientsFinished = clientsFinished;
    }

    @Override
    public void run() {
        try {
            String inputData;
            while (clientsFinished.get() != 5 && (inputData = in.readLine()) != null) {
                processData(inputData);
            }
        } catch (IOException e) {
            System.err.println("Error while reading from clients: " + e.getMessage());
        }
    }

    private void sendData() throws ExecutionException {
        CompletableFuture<CountryList> result = server.handleCountryRankingsRequest();
        while (!result.isDone()) {
            result.join();
        }

        try {
            CountryList countryList = result.get();

            CountryNode currentMyNode = countryList.getHeadElement();
            StringBuilder batchMessage = new StringBuilder();
            batchMessage.append("begin\n");
            while (currentMyNode != null) {
                batchMessage.append(currentMyNode.country)
                        .append(",")
                        .append(currentMyNode.score)
                        .append("\n");
                currentMyNode = currentMyNode.next;
            }
            batchMessage.append("end\n");
            out.println(batchMessage);
        } catch (InterruptedException ignored) {
        }
    }

    private void sendRanking() {
        clientsFinished.incrementAndGet();

        MyNode currentMyNode = list.getHeadElement();
        StringBuilder batchMessage = new StringBuilder();
        batchMessage.append("begin\n");
        while (currentMyNode != null) {
            batchMessage.append(currentMyNode.id)
                    .append(",")
                    .append(currentMyNode.score)
                    .append(",")
                    .append(currentMyNode.country)
                    .append("\n");
            currentMyNode = currentMyNode.next;
        }

        CompletableFuture<CountryList> result = server.handleCountryRankingsRequest();
        while (!result.isDone()) {
            result.join();
        }

        try {
            CountryList countryList = result.get();

            CountryNode currentMyNodeCountry = countryList.getHeadElement();
            while (currentMyNodeCountry != null) {
                batchMessage.append(currentMyNodeCountry.country)
                        .append(",")
                        .append(currentMyNodeCountry.score)
                        .append("\n");
                currentMyNodeCountry = currentMyNodeCountry.next;
            }
            batchMessage.append("end\n");
            out.println(batchMessage);
        } catch (InterruptedException | ExecutionException ignored) {
        }
    }


    private void processData(String data) throws IOException {
        if (data.equals("Cerere clasament tari.")) {
            try {
                sendData();
            } catch (Exception ignored) {

            }
            return;
        }
        if (data.equals("Cerere clasament final.")) {
            sendRanking();
        }
        String[] lines = data.split("\n");
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 2) {
                String id = parts[0];
                int score = Integer.parseInt(parts[1]);
                queue.append(id, score, id.substring(id.length() - 2));
            }
        }
    }
}
