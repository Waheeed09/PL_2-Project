package models;

import java.util.HashMap;
import java.util.Map;

public class Student extends User {

    private Map<Integer, String> answers = new HashMap<>();

    public Student(int id, String name, String email, String password) {
        super(id, name, email, password, "student");
    }

    public void answerQuestion(int questionId, String answerText) {
        answers.put(questionId, answerText);
    }

    public String getAnswerForQuestion(int questionId) {
        return answers.get(questionId);
    }
}
