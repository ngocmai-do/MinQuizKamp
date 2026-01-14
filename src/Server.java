import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    private int port = 12346;

    void main() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            IO.println("Game Server is running");
            while (true) {
                GameProtocol gameProtocol = new GameProtocol();

                PlayerHandler player1 = new PlayerHandler(serverSocket.accept(), 1, gameProtocol);
                PlayerHandler player2 = new PlayerHandler(serverSocket.accept(), 2, gameProtocol);
                gameProtocol.startGame(player1, player2);
            }
        }
    }
}
