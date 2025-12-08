package models;

import java.util.ArrayList;
import java.util.List;

public class Exam {
    private String examId;
    private String subjectCode;
    private String title;
    private int durationMinutes;
    private List<Question> questions;
    private String createdBy; // Lecturer ID
    private boolean isPublished;
    
    public Exam(String examId, String subjectCode, String title, int durationMinutes, String createdBy) {
        this.examId = examId;
        this.subjectCode = subjectCode;
        this.title = title;
        this.durationMinutes = durationMinutes;
        this.createdBy = createdBy;
        this.questions = new ArrayList<>();
        this.isPublished = false;
    }
    
    public void addQuestion(Question question) {
        questions.add(question);
    }
    
    public int getTotalMarks() {
        return questions.stream().mapToInt(Question::getMarks).sum();
    }
    
    // Getters & Setters
    public String getExamId() { return examId; }
    public String getSubjectCode() { return subjectCode; }
    public String getTitle() { return title; }
    public int getDurationMinutes() { return durationMinutes; }
    public List<Question> getQuestions() { return questions; }
    public String getCreatedBy() { return createdBy; }
    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }
    
    // Convert to String for file storage
    public String toFileString() {
        return String.format("%s|%s|%s|%d|%s|%b", 
            examId, subjectCode, title, durationMinutes, createdBy, isPublished);
    }
    
    public static Exam fromFileString(String line) {
        String[] parts = line.split("\\|");
        Exam exam = new Exam(parts[0], parts[1], parts[2], 
                            Integer.parseInt(parts[3]), parts[4]);
        exam.setPublished(Boolean.parseBoolean(parts[5]));
        return exam;
    }
}