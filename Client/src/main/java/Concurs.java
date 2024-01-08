import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class Concurs {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int deltaX;
    private CountDownLatch latch;

    public Concurs(int deltaX) {
        this.deltaX = deltaX;
    }

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        latch = new CountDownLatch(5);
    }

    public void sendMessage(String msg) throws InterruptedException {
        out.println(msg);
        Thread.sleep(deltaX);
    }

    public synchronized StringBuilder receiveMessage() throws IOException {
        String line;
        StringBuilder result = new StringBuilder();
        line = in.readLine();
        while (!(line.equals("end"))) {
            if (!(line.equals("begin"))) {
                result.append(line);
                result.append('\n');
            }
            line = in.readLine();
        }
        return result;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public void waitForTerminat() throws InterruptedException {
        latch.await();
    }

    public synchronized void incrementNrReadFiles() {
        latch.countDown();
    }
}
