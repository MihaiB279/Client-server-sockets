import java.io.*;
import java.util.List;

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

    public int readBatchFromFile(String fileName, int batchSize) {
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

                if (perechiCount == batchSize) {
                    concurs.sendMessage(batchMessage.toString());
                    //clear
                    batchMessage.setLength(0);
                    batchSize = 20;
                    perechiCount = 0;
                }
            }
            if (perechiCount != 0) {
                concurs.sendMessage(batchMessage.toString());
                batchMessage.setLength(0);
                batchSize = 20;
                perechiCount = 0;
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
        // Citirea fișierelor și trimiterea lor la server

        int batchSize = 20;
        for (int i = startIndex; i < endIndex; i++) {
            String fileName = fileNamesList.get(i);

            batchSize = readBatchFromFile(fileName, batchSize);
            sendRequestCountryRanking();
        }

        concurs.incrementNrReadFiles(); //10 pb pt fiecare tara
        sendRequestFinalRanking();
    }

    private void sendRequestCountryRanking() {
        try {
            concurs.sendMessage("Cerere clasament tari.");
            StringBuilder response = concurs.receiveMessage();
//            try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\MihaiBucur\\Desktop\\Facultate anul 3\\PPD\\Client-server-sockets\\Client\\src\\main\\java\\results\\Ranking_" + numeTara))) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\aless\\OneDrive\\Documente\\2023 Year 3\\Proiect PPD\\Client-server-sockets\\Client\\src\\main\\java\\results\\Ranking_" + numeTara))) {
                writer.write(response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRequestFinalRanking() {
        try {
            concurs.waitForTerminat();
            concurs.sendMessage("Cerere clasament final.");
            StringBuilder response = concurs.receiveMessage();
//            try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\MihaiBucur\\Desktop\\Facultate anul 3\\PPD\\Client-server-sockets\\Client\\src\\main\\java\\results\\Final_Ranking_" + numeTara))) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\aless\\OneDrive\\Documente\\2023 Year 3\\Proiect PPD\\Client-server-sockets\\Client\\src\\main\\java\\results\\Final_Ranking_" + numeTara))) {
                writer.write(response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
