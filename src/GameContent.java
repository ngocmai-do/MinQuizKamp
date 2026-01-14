
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameContent {
    public GameState state = GameState.SENDING_CATEGORY;
    public String category = "";

    private int currentQuestion = 0;  //keep check of which question (start with 0 because this is used to get index from list of questions)
    private int currentRound = 1;  //keep check of round, start with 1 for ease of understanding

    public static final String CONTINUE = ", Fortsätta? (y/n)";
    public static final String CORRECT_ANSWER = "Rätt svar!!! Grattis!!!";
    public static final String WRONG_ANSWER = "Fel! Rätt svar är: ";
    public static final String BYE = "Hej då";

    public Integer getTotalPoint() {
        return totalPoint;
    }
    public Integer getPointEachRound() {
        return pointEachRound;
    }

    private Integer totalPoint = 0;
    private Integer pointEachRound = 0;

    QuizData quizData = new QuizData();


    public String processInput(String input) {
        String output = null;

        switch (state) {

            case SENDING_CATEGORY:
                quizData.readQuizData();
                List<String> toSendCategory = new ArrayList<>();
                Collections.shuffle(quizData.categories);
                for (int i = 0; i < 3; i++) {
                    toSendCategory.add(quizData.categories.get(i).getName());
                }
                output = "Välj en kategori: " + toSendCategory.toString();
                state = GameState.CHECKING_CATEGORY;
                break;

            case CHECKING_CATEGORY:
                category = input.trim();
                pointEachRound = 0;
                if (!quizData.isValidCategory(category)) {
                    output = "Den kategorin finns inte!";
                    state = GameState.SENDING_CATEGORY;
                }
                else {
                    output = "Kategori " + category + " vald " + CONTINUE;
                    state = GameState.SENDING_QUESTION;
                }
                break;

            case WAITING_FOR_OTHER_PLAYER_TO_SET_CATEGORY:
                pointEachRound = 0;
                output = "Väntar på att den andra spelaren väljer kategori...";
                break;

            case SENDING_QUESTION:
                if (input.equalsIgnoreCase("y")) {
                    output = quizData.getQuiz(category, currentQuestion);
                    state = GameState.CHECKING_ANSWER;
                } else {
                    output = BYE;  // this two lines to be removed once having GUI
                    resetGame();  // the idea is player can only press "Continue" button after each question, meaning they have no other choice, only yes
                }
                break;

            case CHECKING_ANSWER:
                boolean correct = input.trim().equalsIgnoreCase(quizData.getQuizAnswer(category, currentQuestion));

                if (correct) {
                    pointEachRound++;
                    totalPoint++;
                }

                boolean isLastQuestion = currentQuestion == quizData.quesPerRound - 1;  // Completed final question of each round
                boolean finishedAllRounds = currentRound == quizData.roundNum;   // Completed all rounds

                if (isLastQuestion) {

                    currentRound++;

                    if (finishedAllRounds) { //TO DO: this is where the game should stop and show result of who win, who lose
                        output = resultMessage(correct);
                        state = GameState.GAME_DONE;
                        //need something to stop the game
                    } else {
                        output = resultMessage(correct);
                        state = GameState.WAITING_FOR_OTHER_PLAYER_TO_BE_DONE;
                    }
                    currentQuestion = 0;


                } else {   // More questions left in the round
                    output = resultMessage(correct) + CONTINUE;
                    state = GameState.RESULT_SENT;
                }
                break;

            case RESULT_SENT:
                if (input.equalsIgnoreCase("y")) {
                    currentQuestion++;
                    output = quizData.getQuiz(category, currentQuestion);
                    state = GameState.CHECKING_ANSWER;
                } else {
                    output = BYE;  // this two lines to be removed once having GUI
                    resetGame();  // the idea is player can only press "Continue" button after each question, meaning they have no other choice, only yes
                }
                break;

            case WAITING_FOR_OTHER_PLAYER_TO_BE_DONE:
                output = "Väntar på att den andra spelaren blir klar...";
                break;

            case GAME_DONE:

            default:
                output = "Okänt tillstånd.";
        }

        return output;

    }

    private String resultMessage(boolean correct) {
        if (correct)
            return CORRECT_ANSWER;
        else
            return WRONG_ANSWER + quizData.getQuizAnswer(category, currentQuestion);
    }

    private void resetGame() {
        state = GameState.SENDING_CATEGORY;
        currentQuestion = 0;
        currentRound = 0;
        totalPoint = 0;
    }

}
