import Server.Server;
import model.MyLinkedList;

import java.util.Map;

import static java.lang.System.exit;

public class Main {
    private static int pw = 4, pr = 4, deltaTime = 1000;

    public static void main(String[] args) {
        //pr = Integer.parseInt(args[0]);
        //pw = Integer.parseInt(args[1]);
        //deltaTime = Integer.parseInt(args[2]);
        try {
            Server server = new Server(pr, pw, deltaTime);
            server.start(6666);
            server.stop();
            exit(1);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}