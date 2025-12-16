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
    
    public Map<Integer, String> getAllAnswers() {
        return answers;
    }
    public void setAnswers(Map<Integer, String> answers) {
        this.answers = answers;
    }
    public void clearAnswers() {
        this.answers.clear();
    }
    public void printAnswers() {
        for (Map.Entry<Integer, String> entry : answers.entrySet()) {
            System.out.println("Question ID: " + entry.getKey() + ", Answer: " + entry.getValue());
        }
    }
    public int getTotalAnsweredQuestions() {
        return answers.size();
    }
    public boolean hasAnsweredQuestion(int questionId) {
        return answers.containsKey(questionId);
    }
    public void removeAnswer(int questionId) {
        answers.remove(questionId);
    }
    public void updateAnswer(int questionId, String newAnswerText) {
        if (answers.containsKey(questionId)) {
            answers.put(questionId, newAnswerText);
        }
    }
    public void clearAnswerForQuestion(int questionId) {
        answers.remove(questionId);
    }
    public boolean isAnswerEmpty(int questionId) {
        String answer = answers.get(questionId);
        return answer == null || answer.trim().isEmpty();
    }
    public Map<Integer, String> getAnsweredQuestions() {
        return new HashMap<>(answers);
    }
    public void setAnswerForQuestion(int questionId, String answerText) {
        answers.put(questionId, answerText);
    }
    public void printAllAnswers() {
        for (Map.Entry<Integer, String> entry : answers.entrySet()) {
            System.out.println("Question ID: " + entry.getKey() + ", Answer: " + entry.getValue());
        }
    }
    public boolean hasAnsweredAnyQuestion() {
        return !answers.isEmpty();
    }
    public void clearAllAnswers() {
        answers.clear();
    }

}

