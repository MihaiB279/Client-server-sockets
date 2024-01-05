import Server.Server;
import model.MyLinkedList;

public class Main {
    private static MyLinkedList myLinkedList;
    private static int pw = 4, pr = 4, deltaTime = 1;

    public static void main(String[] args) {
    /*
        myLinkedList = new MyLinkedList();

        List<Path> filePaths = new ArrayList<>();

        try {
            Files.walk(Paths.get(folderPath))
                    .filter(Files::isRegularFile)
                    .forEach(filePaths::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (p == 1) {
            long startTime = System.nanoTime(), endTime;
            try {
                for (Path filePath : filePaths) {
                    processFile(filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            myLinkedList.printToFile("secvResult.txt");
            endTime = System.nanoTime();
            System.out.println((double) (endTime - startTime) / 1E6);
        } else {
            int filesPerProducer = filePaths.size() / pr;
            int filesPerProducerRest = filePaths.size() % pr;

            List<Boolean> producerFlags = new ArrayList<>();
            List<Boolean> consumerFlags = new ArrayList<>();
            for (int i = 0; i < pr; i++) {
                producerFlags.add(false);
            }

            for (int i = 0; i < p - pr; i++) {
                consumerFlags.add(false);
            }

            int start = 0;
            int end = start + filesPerProducer;
            MySynchronizedQueue queue = new MySynchronizedQueue(producerFlags);
            for (int i = 0; i < pr; i++) {
                if (filesPerProducerRest > 0) {
                    end++;
                    filesPerProducerRest--;
                }
                Path[] filesForProducer = filePaths.subList(start, end).toArray(new Path[0]);
                start = end;
                end += filesPerProducer;

                Producer producer = new Producer(queue, filesForProducer, producerFlags, i, consumerFlags, myLinkedList);
                Thread producerThread = new Thread(producer);
                producerThread.start();
            }

            for (int i = 0; i < p - pr; i++) {
                Consumer consumer = new Consumer(queue, myLinkedList, producerFlags, consumerFlags, i);
                Thread consumerThread = new Thread(consumer);
                consumerThread.start();
            }
        }
    }

    private static void processFile(Path filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    int id = Integer.parseInt(parts[0]);
                    int score = Integer.parseInt(parts[1]);
                    myLinkedList.append(id, score);
                }
            }
        }
    }*/
        //pr = Integer.parseInt(args[0]);
        //pw = Integer.parseInt(args[1]);
        //deltaTime = Integer.parseInt(args[2]);
        try{
            Server server = new Server(pr, pw, deltaTime);
            server.start(6666);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}