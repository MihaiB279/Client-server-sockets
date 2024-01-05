package org.example;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Tara implements Runnable {
    private final Concurs concurs;
    private String numeTara;
    private int startIndex;
    private int endIndex;
    private List<String> fileNamesList;
    private StringBuilder batchMessage;

    public Tara(Concurs concurs, String numeTara, int startIndex, int endIndex, List<String> fileNamesList) {
        this.concurs = concurs;
        this.numeTara = numeTara;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.fileNamesList = fileNamesList;
        this.batchMessage = new StringBuilder();
    }

    public int readBatchFromFile(String fileName, int batchSize)
    {
        int perechiCount = 0;

        try {
            File file = new File(fileName);
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");

                // Verificăm dacă avem ID și punctaj în linie
                if (parts.length == 2) {
                    String ID = parts[0];
                    int punctaj = Integer.parseInt(parts[1]);

                    batchMessage.append(ID)
                                .append(",")
                                .append(punctaj)
                                .append("\n");

                    perechiCount++;
                }

                if (perechiCount == batchSize)
                {
                    concurs.sendMessage(batchMessage.toString());
                    //clear
                    batchMessage.setLength(0);
                    batchSize=20;
                    perechiCount = 0;
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Eroare la citirea din fisier: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return 20 - perechiCount;
    }

    @Override
    public void run() {
        // Citirea fișierelor și adăugarea în coadă

        int batchSize = 20;
        for (int i = startIndex; i <endIndex; i++) {
            String fileName = fileNamesList.get(i);

            batchSize = readBatchFromFile(fileName, batchSize);
        }

        concurs.incrementNrReadFiles(10); //10 pb pt fiecare tara

        sendRequestCountryRanking();
        sendRequestFinalRanking();
    }

    private void sendRequestCountryRanking() {
        try {
            concurs.sendMessage("Cerere clasament tari.");
            StringBuilder response = concurs.receiveMessage();
            System.out.println(Thread.currentThread().getName());
            System.out.println(response);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private synchronized void sendRequestFinalRanking() {
        while(!concurs.isTerminat())
        {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            concurs.sendMessage("Cerere clasament final.");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
