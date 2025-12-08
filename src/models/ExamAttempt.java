package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExamAttempt {
    private String studentId;
    private String examId;
    private LocalDateTime attemptDate;
    private boolean hasAttempted;
    
    public ExamAttempt(String studentId, String examId) {
        this.studentId = studentId;
        this.examId = examId;
        this.attemptDate = LocalDateTime.now();
        this.hasAttempted = true;
    }
    
    // Getters
    public String getStudentId() { return studentId; }
    public String getExamId() { return examId; }
    public LocalDateTime getAttemptDate() { return attemptDate; }
    public boolean hasAttempted() { return hasAttempted; }
    
    // Convert to String for file storage
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("%s|%s|%s|%b",
            studentId, examId, attemptDate.format(formatter), hasAttempted);
    }
    
    public static ExamAttempt fromFileString(String line) {
        String[] parts = line.split("\\|");
        ExamAttempt attempt = new ExamAttempt(parts[0], parts[1]);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        attempt.attemptDate = LocalDateTime.parse(parts[2], formatter);
        attempt.hasAttempted = Boolean.parseBoolean(parts[3]);
        
        return attempt;
    }
}