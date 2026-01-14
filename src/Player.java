import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Player {
    private String ip = "127.0.0.1";
    private int port = 12346;
    Thread thread;




    public void play() throws IOException {
//        int playerNum;
//        int opponentNum;
        try (
           Socket socket = new Socket(ip, port);
           PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
           BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           BufferedReader playerReader = new BufferedReader(new InputStreamReader(System.in));
        ) {

            thread = new Thread(() -> {
                try {
                    String fromServer;
                    while ((fromServer = in.readLine()) != null) {
                        if (fromServer.startsWith("MEDDELANDE")) {
                            IO.println(fromServer.substring(11));
                        }
                        else {
                            IO.println(fromServer);
                        }
                    }
                } catch (IOException e) {}
            });
            thread.start();

            String playerAnswer;
            while ((playerAnswer = playerReader.readLine()) != null) {
                out.println(playerAnswer);
            }
        } finally {
            thread.interrupt();
        }
    }

    public static void main (String[] args) throws Exception {
        Player player = new Player();
        player.play();
    }
}
