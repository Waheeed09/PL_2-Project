package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Feedback {
    private String feedbackId;
    private String studentId;
    private String examId;
    private int difficultyRating; // 1-5 (1=Very Easy, 5=Very Hard)
    private int clarityRating; // 1-5
    private String comments;
    private LocalDateTime submissionDate;
    
    public Feedback(String feedbackId, String studentId, String examId,
                   int difficultyRating, int clarityRating, String comments) {
        this.feedbackId = feedbackId;
        this.studentId = studentId;
        this.examId = examId;
        this.difficultyRating = difficultyRating;
        this.clarityRating = clarityRating;
        this.comments = comments;
        this.submissionDate = LocalDateTime.now();
    }
    
    // Getters
    public String getFeedbackId() { return feedbackId; }
    public String getStudentId() { return studentId; }
    public String getExamId() { return examId; }
    public int getDifficultyRating() { return difficultyRating; }
    public int getClarityRating() { return clarityRating; }
    public String getComments() { return comments; }
    public LocalDateTime getSubmissionDate() { return submissionDate; }
    
    // Convert to String for file storage
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("%s|%s|%s|%d|%d|%s|%s",
            feedbackId, studentId, examId, difficultyRating, clarityRating,
            comments.replace("|", ""), submissionDate.format(formatter));
    }
    
    public static Feedback fromFileString(String line) {
        String[] parts = line.split("\\|");
        Feedback feedback = new Feedback(
            parts[0], parts[1], parts[2],
            Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), parts[5]
        );
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        feedback.submissionDate = LocalDateTime.parse(parts[6], formatter);
        
        return feedback;
    }
}