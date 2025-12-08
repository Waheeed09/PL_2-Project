package models;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private List<String> registeredSubjects;
    private List<String> completedExams;
    
    public Student(String userId, String name, String password, String email) {
        super(userId, name, password, email, "STUDENT");
        this.registeredSubjects = new ArrayList<>();
        this.completedExams = new ArrayList<>();
    }
    
    @Override
    public boolean hasPermission(String action) {
        // Students can only access their own data
        List<String> allowedActions = List.of(
            "VIEW_OWN_EXAMS", 
            "TAKE_EXAM", 
            "VIEW_RESULTS",
            "SUBMIT_FEEDBACK",
            "REQUEST_RECORRECTION"
        );
        return allowedActions.contains(action);
    }
    
    public void addRegisteredSubject(String subjectCode) {
        if (!registeredSubjects.contains(subjectCode)) {
            registeredSubjects.add(subjectCode);
        }
    }
    
    public void markExamCompleted(String examId) {
        if (!completedExams.contains(examId)) {
            completedExams.add(examId);
        }
    }
    
    public boolean hasCompletedExam(String examId) {
        return completedExams.contains(examId);
    }
    
    // Getters
    public List<String> getRegisteredSubjects() { return registeredSubjects; }
    public List<String> getCompletedExams() { return completedExams; }
    
    // Convert to String for file storage
    public String toFileString() {
        String subjects = String.join(",", registeredSubjects);
        String exams = String.join(",", completedExams);
        return String.format("%s|%s|%s|%s|%s|%s", 
            userId, name, password, email, subjects, exams);
    }
    
    // Create Student from file line
    public static Student fromFileString(String line) {
        String[] parts = line.split("\\|");
        Student student = new Student(parts[0], parts[1], parts[2], parts[3]);
        
        if (parts.length > 4 && !parts[4].isEmpty()) {
            String[] subjects = parts[4].split(",");
            for (String subject : subjects) {
                student.addRegisteredSubject(subject);
            }
        }
        
        if (parts.length > 5 && !parts[5].isEmpty()) {
            String[] exams = parts[5].split(",");
            for (String exam : exams) {
                student.markExamCompleted(exam);
            }
        }
        
        return student;
    }
}