package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Result {
    private String resultId;
    private String studentId;
    private String examId;
    private Map<String, String> studentAnswers; // questionId -> answer
    private int score;
    private int totalMarks;
    private LocalDateTime submissionTime;
    private boolean isPublished;
    
    public Result(String resultId, String studentId, String examId, int totalMarks) {
        this.resultId = resultId;
        this.studentId = studentId;
        this.examId = examId;
        this.totalMarks = totalMarks;
        this.studentAnswers = new HashMap<>();
        this.score = 0;
        this.submissionTime = LocalDateTime.now();
        this.isPublished = false;
    }
    
    public void addAnswer(String questionId, String answer) {
        studentAnswers.put(questionId, answer);
    }
    
    public void calculateScore(Exam exam) {
        score = 0;
        for (Question question : exam.getQuestions()) {
            String studentAnswer = studentAnswers.get(question.getQuestionId());
            if (question.checkAnswer(studentAnswer)) {
                score += question.getMarks();
            }
        }
    }
    
    public double getPercentage() {
        return totalMarks > 0 ? (score * 100.0) / totalMarks : 0;
    }
    
    public String getGrade() {
        double percentage = getPercentage();
        if (percentage >= 90) return "A+";
        if (percentage >= 85) return "A";
        if (percentage >= 80) return "B+";
        if (percentage >= 75) return "B";
        if (percentage >= 70) return "C+";
        if (percentage >= 65) return "C";
        if (percentage >= 60) return "D+";
        if (percentage >= 50) return "D";
        return "F";
    }
    
    // Getters & Setters
    public String getResultId() { return resultId; }
    public String getStudentId() { return studentId; }
    public String getExamId() { return examId; }
    public int getScore() { return score; }
    public int getTotalMarks() { return totalMarks; }
    public LocalDateTime getSubmissionTime() { return submissionTime; }
    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }
    public Map<String, String> getStudentAnswers() { return studentAnswers; }
    
    // Convert to String for file storage
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder answersStr = new StringBuilder();
        for (Map.Entry<String, String> entry : studentAnswers.entrySet()) {
            answersStr.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
        }
        if (answersStr.length() > 0) {
            answersStr.setLength(answersStr.length() - 1); // Remove last comma
        }
        
        return String.format("%s|%s|%s|%d|%d|%s|%b|%s",
            resultId, studentId, examId, score, totalMarks,
            submissionTime.format(formatter), isPublished, answersStr.toString());
    }
    
    public static Result fromFileString(String line) {
        String[] parts = line.split("\\|");
        Result result = new Result(parts[0], parts[1], parts[2], Integer.parseInt(parts[4]));
        result.score = Integer.parseInt(parts[3]);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        result.submissionTime = LocalDateTime.parse(parts[5], formatter);
        result.isPublished = Boolean.parseBoolean(parts[6]);
        
        if (parts.length > 7 && !parts[7].isEmpty()) {
            String[] answers = parts[7].split(",");
            for (String answer : answers) {
                String[] pair = answer.split(":");
                if (pair.length == 2) {
                    result.addAnswer(pair[0], pair[1]);
                }
            }
        }
        
        return result;
    }
}