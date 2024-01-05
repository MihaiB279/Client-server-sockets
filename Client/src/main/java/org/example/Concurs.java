package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Concurs {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int deltaT;

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

    public String receiveMessage() throws IOException {
        return in.readLine();
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

//    public void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() throws IOException {
//        startConnection("127.0.0.1", 6666);
//        String response = sendMessage("hello server");
//
////        stopConnection();
//    }

}
