package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Concurs {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int deltaT;
    private int nrReadFiles = 0;
    private final Lock lock = new ReentrantLock();
    private boolean terminat = false;

    public Concurs(int deltaT) {
        this.deltaT = deltaT;
    }

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void sendMessage(String msg) throws InterruptedException {
        out.println(msg);
        Thread.sleep(deltaT);
    }

    public synchronized StringBuilder receiveMessage() throws IOException {
        //read multiple lines fromm the result, not done yet
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = in.readLine()) != null) {
            result.append(line);
            result.append('\n');
        }
        return result;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public synchronized void setTerminat(boolean terminat) {
        this.terminat = terminat;
    }
    public synchronized boolean isTerminat() {
        return this.terminat;
    }

    public synchronized void incrementNrReadFiles(int nrFiles) {
        lock.lock();
        this.nrReadFiles += nrFiles;
        System.out.println("Read files: " + nrReadFiles);
        if (nrReadFiles==50) {
            setTerminat(true);
            notifyAll();
        }
        lock.unlock();
    }

}
