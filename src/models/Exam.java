package models;

import java.util.ArrayList;
import java.util.List;

public class Exam {

    private String examId;
    private String title;
    private String subjectId;
    private String lecturerId;
    private int durationMinutes;

    private List<Question> questions;
    private List<Result> results;

    public Exam(String examId, String title,
                String subjectId, String lecturerId,
                int durationMinutes) {

        this.examId = examId;
        this.title = title;
        this.subjectId = subjectId;
        this.lecturerId = lecturerId;
        this.durationMinutes = durationMinutes;
        this.questions = new ArrayList<>();
        this.results = new ArrayList<>();
    }

    // ===== Getters =====
    public String getExamId() {
        return examId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public List<Result> getResults() {
        return results;
    }

    // ===== Logic =====
    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void updateQuestion(int index, Question newQuestion) {
        if (index >= 0 && index < questions.size()) {
            questions.set(index, newQuestion);
        }
    }

    public void addResult(Result result) {
        results.add(result);
    }

    public int getTotalGrade() {
        int total = 0;
        for (Question q : questions) {
            total += q.getPoints();
        }
        return total;
    }
}
