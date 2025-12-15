package models;

import java.util.ArrayList;
import java.util.List;

public class Exam {

    private String examId;
    private String title;
    private List<Question> questions;

    public Exam(String examId, String title) {
        this.examId = examId;
        this.title = title;
        this.questions = new ArrayList<>();
    }

    public String getExamId() {
        return examId;
    }

    public String getTitle() {
        return title;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
