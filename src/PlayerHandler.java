import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PlayerHandler extends Thread {
    int playerNum;
    PlayerHandler opponent;
    Socket socket;
    GameProtocol gameProtocol;
    GameContent gameContent = new GameContent();
    PrintWriter out;
    BufferedReader in;

    public String getPlayerName() {
        return "Spelare " + playerNum;
    }

    public PlayerHandler(Socket socket, int playerNum, GameProtocol gameProtocol) throws IOException {
        this.socket = socket;
        this.playerNum = playerNum;
        this.gameProtocol = gameProtocol;

        out = new PrintWriter(socket.getOutputStream(), true);   //reader and writer are put in constructor to make sure that it always have in/out function when it is created
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println("VÄLKOMMEN Spelare " + playerNum);
    }

    public void setOpponent (PlayerHandler opponent) {
        this.opponent = opponent;
    }

    void sendResponse(String output) {
        out.println(output);
    }

    public PlayerHandler getOpponent () {
        return opponent;
    }

    public void run () {
        try {
            out.println("MEDDELANDE Alla spelare är uppkopplade");

            String inputLine = null;

            while ((inputLine = in.readLine()) != null) {
                gameProtocol.receivePlayerInput(this, inputLine);
            }
        } catch (IOException e) {
            System.out.println("Player died: " + e);
        } finally {
            try {socket.close();} catch (IOException e) {}
        }
    }
}
