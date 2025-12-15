package models;

import java.util.List;

public class Question {

    private int id;
    private String examId;
    private String text;                 // نص السؤال
    private String correctAnswer;         // الإجابة الصحيحة
    private String type;                 // MC / TF / SA
    private List<String> options;         // اختيارات (لو موجودة)
    private int points;                  // درجة السؤال

    // Constructor كامل (للاستخدام الأساسي)
    public Question(int id, String examId, String text,
                    String correctAnswer, String type,
                    List<String> options, int points) {

        this.id = id;
        this.examId = examId;
        this.text = text;
        this.correctAnswer = correctAnswer;
        this.type = type;
        this.options = options;
        this.points = points;
    }

    // Constructor بسيط (عشان الكود القديم ميقعش)
    public Question(int id, String text, String correctAnswer) {
        this.id = id;
        this.text = text;
        this.correctAnswer = correctAnswer;
        this.points = 1;
    }

    // ================= Logic =================

    public boolean isCorrectAnswer(String answer) {
        if (answer == null) return false;
        return correctAnswer.equalsIgnoreCase(answer.trim());
    }

    // ================= Getters =================

    public int getId() {
        return id;
    }

    public String getExamId() {
        return examId;
    }

    public String getText() {
        return text;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    // دعم الكود القديم اللي بيستخدم getCorrect()
    public String getCorrect() {
        return correctAnswer;
    }

    public String getType() {
        return type;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getPoints() {
        return points;
    }

    // ================= Setters =================

    public void setText(String text) {
        this.text = text;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    // دعم الكود القديم
    public void setCorrect(String correct) {
        this.correctAnswer = correct;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
