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

    public Producer(Server server, Socket socket, MySynchronizedQueue queue, MyLinkedList list, AtomicInteger clientsFinished) {
        this.server = server;
        this.queue = queue;
        this.list = list;
        this.clientSocket = socket;
        this.clientsFinished = clientsFinished;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputData;
            while ((inputData = in.readLine()) != null) {
                processData(inputData);
            }
        } catch (IOException e) {
            System.err.println("Error while reading from clients: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error while reading from clients: " + e.getMessage());
            }
        }
    }

    private void sendData() throws IOException, ExecutionException {
        CompletableFuture<CountryList> result = server.handleCountryRankingsRequest();
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        while (!result.isDone()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        try {
            CountryList countryList = result.get();

            CountryNode currentMyNode = countryList.getHeadElement();
            StringBuilder batchMessage = new StringBuilder();
            while (currentMyNode != null) {
                batchMessage.append(currentMyNode.country)
                        .append(",")
                        .append(currentMyNode.score)
                        .append("\n");
                currentMyNode = currentMyNode.next;
            }
            out.println(batchMessage);
        } catch (InterruptedException ignored) {
        }
    }

    private void sendRanking() throws IOException {
        clientsFinished.incrementAndGet();

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        MyNode currentMyNode = list.getHeadElement();
        StringBuilder batchMessage = new StringBuilder();
        while (currentMyNode != null) {
            batchMessage.append(currentMyNode.country)
                    .append(",")
                    .append(currentMyNode.score)
                    .append("\n");
            currentMyNode = currentMyNode.next;
        }

        CompletableFuture<CountryList> result = server.handleCountryRankingsRequest();
        while (!result.isDone()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        try {
            CountryList countryList = result.get();

            CountryNode currentMyNodeCountry = countryList.getHeadElement();
            batchMessage = new StringBuilder();
            while (currentMyNode != null) {
                batchMessage.append(currentMyNodeCountry.country)
                        .append(",")
                        .append(currentMyNodeCountry.score)
                        .append("\n");
                currentMyNodeCountry = currentMyNodeCountry.next;
            }
            out.println(batchMessage);
        } catch (InterruptedException ignored) {
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
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
