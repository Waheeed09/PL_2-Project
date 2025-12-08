package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RecorrectionRequest {
    private String requestId;
    private String studentId;
    private String examId;
    private String resultId;
    private String reason;
    private LocalDateTime requestDate;
    private String status; // PENDING, APPROVED, REJECTED
    private String adminResponse;
    
    public RecorrectionRequest(String requestId, String studentId, 
                              String examId, String resultId, String reason) {
        this.requestId = requestId;
        this.studentId = studentId;
        this.examId = examId;
        this.resultId = resultId;
        this.reason = reason;
        this.requestDate = LocalDateTime.now();
        this.status = "PENDING";
        this.adminResponse = "";
    }
    
    // Getters & Setters
    public String getRequestId() { return requestId; }
    public String getStudentId() { return studentId; }
    public String getExamId() { return examId; }
    public String getResultId() { return resultId; }
    public String getReason() { return reason; }
    public LocalDateTime getRequestDate() { return requestDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAdminResponse() { return adminResponse; }
    public void setAdminResponse(String response) { this.adminResponse = response; }
    
    // Convert to String for file storage
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("%s|%s|%s|%s|%s|%s|%s|%s",
            requestId, studentId, examId, resultId, 
            reason.replace("|", ""), // Remove pipes from reason
            requestDate.format(formatter), status, adminResponse.replace("|", ""));
    }
    
    public static RecorrectionRequest fromFileString(String line) {
        String[] parts = line.split("\\|");
        RecorrectionRequest request = new RecorrectionRequest(
            parts[0], parts[1], parts[2], parts[3], parts[4]
        );
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        request.requestDate = LocalDateTime.parse(parts[5], formatter);
        request.status = parts[6];
        if (parts.length > 7) {
            request.adminResponse = parts[7];
        }
        
        return request;
    }
}