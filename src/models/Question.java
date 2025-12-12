package models;

public class Question {
    public String examId;
    public String text;
    public String correct;

    public Question(String examId, String text, String correct) {
        this.examId = examId;
        this.text = text;
        this.correct = correct;
    }
}
