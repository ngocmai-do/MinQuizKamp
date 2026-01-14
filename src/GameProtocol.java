import java.util.ArrayList;
import java.util.List;

public class GameProtocol {
    private PlayerHandler player1;
    private PlayerHandler player2;
    private PlayerHandler lastPlayerChoseCategory;
    private List<String> resultListEachRound = new ArrayList<>();

    public synchronized void receivePlayerInput (PlayerHandler player, String inputLine) {
        GameContent gameContent = player.gameContent;
        GameState previousState = gameContent.state;

        String outputLine;
        outputLine = gameContent.processInput(inputLine);
        player.sendResponse(outputLine);

        boolean wasSelectingCategory = (previousState == GameState.CHECKING_CATEGORY);

        // the following section ensure a category is selected by one player before setting the state of the other player
        if (wasSelectingCategory && gameContent.state == GameState.SENDING_QUESTION) {
            onCategorySelectedByOpponent(player.opponent, gameContent.category);
        }

        // this ensures that both player are finished with their round before moving to the next
        if (player1.gameContent.state == GameState.WAITING_FOR_OTHER_PLAYER_TO_BE_DONE
                && player2.gameContent.state == GameState.WAITING_FOR_OTHER_PLAYER_TO_BE_DONE) {
            sendScoreboardToPlayers();
            switchCategoryChooser();
        }

        if (player1.gameContent.state == GameState.GAME_DONE && player2.gameContent.state == GameState.GAME_DONE) {
            sendScoreboardToPlayers();
            sendGameFinalResult();
        }
    }

    public void onCategorySelectedByOpponent(PlayerHandler player, String category) {
        player.sendResponse("Kategori " + category + " vald, Fortsätta? (y/n)");
        player.gameContent.state = GameState.SENDING_QUESTION;
        player.gameContent.category = category;
    }


    //this method start client-server connection, create threads, allow player 1 to choose category first
    public void startGame (PlayerHandler player1, PlayerHandler player2) {
        this.player1 = player1;
        this.player2 = player2;
        player1.setOpponent(player2);
        player2.setOpponent(player1);
        player1.start();
        player2.start();

        player1.gameContent.state = GameState.SENDING_CATEGORY;
        player2.gameContent.state = GameState.WAITING_FOR_OTHER_PLAYER_TO_SET_CATEGORY;

        receivePlayerInput(player1, null);
        receivePlayerInput(player2, null);
        lastPlayerChoseCategory = player1;
    }

    public void switchCategoryChooser() {
        PlayerHandler newPlayerChooseCategory;
        if (lastPlayerChoseCategory == player1) {
            newPlayerChooseCategory = player2;
        } else {
            newPlayerChooseCategory = player1;
        }

        newPlayerChooseCategory.gameContent.state = GameState.SENDING_CATEGORY;
        lastPlayerChoseCategory.gameContent.state = GameState.WAITING_FOR_OTHER_PLAYER_TO_SET_CATEGORY;

        lastPlayerChoseCategory = newPlayerChooseCategory;  //reset so next round would flip again

        receivePlayerInput(player1, null);
        receivePlayerInput(player2, null);
    }


    private void sendScoreboardToPlayers() {
        String result = player1.getPlayerName() + ": " + player1.gameContent.getPointEachRound() + " poäng\n" +
                player2.getPlayerName() + ": " + player2.gameContent.getPointEachRound() + " poäng\n";

        resultListEachRound.add(result);

        for (int i = 0; i < resultListEachRound.size(); i++) {
            String resultMess = "---- Round " + (i + 1) + " ----\n" + resultListEachRound.get(i);
            player1.sendResponse(resultMess);
            player2.sendResponse(resultMess);
        }

    }

    private void sendGameFinalResult() {
        String finalResult;
        String result = "---- Totalt antal poäng ----\n"
                + player1.getPlayerName() + ": " + player1.gameContent.getTotalPoint() + " poäng\n" +
                player2.getPlayerName() + ": " + player2.gameContent.getTotalPoint() + " poäng\n";
        if (player1.gameContent.getTotalPoint() < player2.gameContent.getTotalPoint()) {
            finalResult = result + "Spelare 2 WIN!!!";
        } else if (player1.gameContent.getTotalPoint() > player2.gameContent.getTotalPoint()) {
            finalResult = result + "Spelare 1 WIN!!!";
        } else {
            finalResult = result + "OAVGJORT";
        }
        player1.sendResponse(finalResult);
        player2.sendResponse(finalResult);
    }
}
