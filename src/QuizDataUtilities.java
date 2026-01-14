import java.util.ArrayList;
import java.util.List;

public class QuizDataUtilities {

    public static class Question {
        private String question;
        private String [] ansAlternativ;
        private String correctAnswer;


        public Question (String question, String [] ansAlternativ, String correctAnswer) {
            this.question = question;
            this.ansAlternativ = ansAlternativ;
            this.correctAnswer = correctAnswer;
        }

        public String getQuestion() {
            return question;
        }

        public String[] getAnsAlternativ() {
            return ansAlternativ;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

    }

    public static class Category {
        public String getName() {
            return name;
        }

        private String name;
        List<Question> questions = new ArrayList<>();

        public Category (String name) {
            this.name = name;
        }
    }

}
