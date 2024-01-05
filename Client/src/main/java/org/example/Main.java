package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        long startTime = System.nanoTime();

//        int p = Integer.parseInt(args[0]); // Numărul de tari ca si threaduri
        int p = 5;
        int deltaT = 1000;

        int nrTotalFiles = 5*10;
        List<String> fileNamesList = new ArrayList<>();

        // Adaugam numele fisierelor intr o lista
        for (int country = 1; country <= 5; country++) {
            String countryName = "C" + country;

            for (int problem = 1; problem <= 10; problem++) {
                String problemName = "P" + problem;
                String fileName = "C:\\Users\\aless\\OneDrive\\Documente\\2023 Year 3\\Proiect PPD\\Client-server-sockets\\Client\\src\\Rezultate" + countryName + "_" + problemName + ".txt";
                fileNamesList.add(fileName);
            }
        }

        Concurs concurs = new Concurs(deltaT);
        try{
            concurs.startConnection("127.0.0.1", 6666);
        } catch(Exception e) {
            System.out.println(e);
        }

        Thread[] threads = new Thread[p];
        int nrFilesPerThread = 10;
        int startIndex = 0;

        // Crearea și pornirea thread-urilor pt fiecare tara
        for (int i = 0; i < p; i++) {
            int endIndex = startIndex + nrFilesPerThread;

            int nrTara = i+1;
            threads[i] = new Thread(new Tara(concurs, "C" + nrTara, startIndex, endIndex, fileNamesList));
            threads[i].start();

            startIndex = endIndex;
        }

        //readers
        for (int i = 0; i < p; i++){
            try{
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Scrie clasamentul final în fișier
//        concurs.scrieClasament();

        long endTime = System.nanoTime();
        long timeDuration = endTime - startTime;
        System.out.println((double)(timeDuration)/1E6);
    }
}