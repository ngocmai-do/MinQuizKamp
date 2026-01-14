import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class QuizData {

    List<String> categoryNameList = new ArrayList<>();
    List<QuizDataUtilities.Category> categories = new ArrayList<>();

    int roundNum;
    int quesPerRound;
    String quizDataPath = "src/QuizData.txt";

    public QuizData () {
        Properties roundQuesNum = new Properties();
        try {
            roundQuesNum.load(new FileInputStream("src/Round&QuesNum.properties"));
        }
        catch (Exception e){
            e.printStackTrace();
        }

        roundNum = Integer.parseInt(roundQuesNum.getProperty("RoundPerGame"));
        quesPerRound = Integer.parseInt(roundQuesNum.getProperty("QuestionsPerRound"));
    }

    public void readQuizData () {   // read quiz data and save into list or array?
        String temp;
        String categoryName;
        String question = "";
        String correctAnswer = "";

        QuizDataUtilities.Category currentCategory = null;

        Scanner sc = null;
        try {
            sc = new Scanner(new File(quizDataPath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        while (sc.hasNextLine()) {
            temp = sc.nextLine().trim();
            if (temp.isEmpty()) continue;

            // get category's name
            if (temp.equals("Category:")) {
                categoryName = sc.nextLine().trim();
                currentCategory = new QuizDataUtilities.Category(categoryName);
                categories.add(currentCategory);
                categoryNameList.add(categoryName);
                continue;
            }

            //get question
            String [] answerAlternativ = new String[4];
            if (!temp.startsWith("1.") && !temp.startsWith("Correct:")) {
                question = temp;

                //get answer alternatives
                for (int i = 0; i < 4; i++) {
                    answerAlternativ[i] = sc.nextLine().trim();
                }

                //get correct answer
                String correctLine = sc.nextLine().trim();
                correctAnswer = correctLine.substring("Correct:".length()).trim();

                currentCategory.questions.add(new QuizDataUtilities.Question(question, answerAlternativ, correctAnswer));
            }
        }
        sc.close();

    }

    public boolean isValidCategory(String category) {
        readQuizData();
        for (QuizDataUtilities.Category cat : categories) {
            if (cat.getName().equalsIgnoreCase(category)) {
                return true;
            }
        }
        return false;
    }

    //get specific question for each around with parameter of choosen catergory provided by player)
    public String getQuiz (String category, int questionNum) {
        readQuizData();
        for (QuizDataUtilities.Category cat : categories) {
            if (cat.getName().equalsIgnoreCase(category)) {
                QuizDataUtilities.Question question = cat.questions.get(questionNum);
                return question.getQuestion() + " " + Arrays.toString(question.getAnsAlternativ());
            }
        }
        return "Quiz not found";
    }

    public String getQuizAnswer (String category, int questionNum) {
        readQuizData();
        for (QuizDataUtilities.Category cat : categories) {
            if (cat.getName().equalsIgnoreCase(category)) {
                QuizDataUtilities.Question question = cat.questions.get(questionNum);
                return question.getCorrectAnswer();
            }
        }
        return "Quiz answer not found";
    }
}
