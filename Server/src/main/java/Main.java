import Server.Server;

public class Main {
    private static int pw = 4, pr = 4, deltaTime = 1;

    public static void main(String[] args) {
        pr = args.length > 0 ? Integer.parseInt(args[0]) : 4;
        pw = args.length > 0 ? Integer.parseInt(args[1]) : 4;
        deltaTime = args.length > 0 ? Integer.parseInt(args[2]) : 1;
        try {
            long startTime = System.nanoTime();
            Server server = new Server(pr, pw, deltaTime);
            server.start(6666);
            server.stop();
            long endTime = System.nanoTime();
            long timeDuration = endTime - startTime;
            System.out.println((double)(timeDuration)/1E6);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}