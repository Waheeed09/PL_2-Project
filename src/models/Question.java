package models;

import java.util.List;

public class Question {

    private int id;
    private String examId;
    private String text;                
    private String correctAnswer;        
    private String type;                 
    private List<String> options;        
    private int points;                  

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

    public Question(int id, String text, String correctAnswer) {
        this.id = id;
        this.text = text;
        this.correctAnswer = correctAnswer;
        this.points = 1;
    }

    public void setExamId(String examId) {
        this.examId = examId;
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

    public int getQuestionId() {
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

    public void setCorrect(String correct) {
        this.correctAnswer = correct;
    }

    public void setPoints(int points) {
        this.points = points;
    }
    public boolean isAnsweredCorrectlyBy(Student student) {
    if (student == null) return false;
    String ans = student.getAnswerForQuestion(this.id);
    return isCorrectAnswer(ans);
}
}
