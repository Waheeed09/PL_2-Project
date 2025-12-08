package services;

import models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultService {
    private static final String RESULTS_FILE = "results.txt";
    private static final String RECORRECTION_FILE = "recorrection_requests.txt";
    private static final String FEEDBACK_FILE = "feedback.txt";
    
    /**
     * Create and save exam result
     */
    public static Result createResult(String studentId, String examId, Map<String, String> answers) {
        // Get exam with questions
        Exam exam = ExamService.getExamWithQuestions(examId);
        if (exam == null) {
            System.out.println("Exam not found");
            return null;
        }
        
        // Generate result ID
        String resultId = FileHandler.generateId(RESULTS_FILE, "R");
        
        // Create result object
        Result result = new Result(resultId, studentId, examId, exam.getTotalMarks());
        
        // Add all answers
        for (Map.Entry<String, String> entry : answers.entrySet()) {
            result.addAnswer(entry.getKey(), entry.getValue());
        }
        
        // Calculate score
        result.calculateScore(exam);
        
        // Save to file
        boolean saved = FileHandler.appendToFile(RESULTS_FILE, result.toFileString());
        
        if (saved) {
            System.out.println("Result created successfully with score: " + 
                             result.getScore() + "/" + result.getTotalMarks());
            return result;
        }
        
        return null;
    }
    
    /**
     * Get result by ID
     */
    public static Result getResult(String resultId) {
        List<String> lines = FileHandler.readFile(RESULTS_FILE);
        
        for (String line : lines) {
            Result result = Result.fromFileString(line);
            if (result.getResultId().equals(resultId)) {
                return result;
            }
        }
        
        return null;
    }
    
    /**
     * Get all results for a student
     */
    public static List<Result> getStudentResults(String studentId) {
        List<Result> results = new ArrayList<>();
        List<String> lines = FileHandler.readFile(RESULTS_FILE);
        
        for (String line : lines) {
            Result result = Result.fromFileString(line);
            if (result.getStudentId().equals(studentId)) {
                results.add(result);
            }
        }
        
        return results;
    }
    
    /**
     * Get published results for a student
     */
    public static List<Result> getPublishedStudentResults(String studentId) {
        List<Result> results = new ArrayList<>();
        List<String> lines = FileHandler.readFile(RESULTS_FILE);
        
        for (String line : lines) {
            Result result = Result.fromFileString(line);
            if (result.getStudentId().equals(studentId) && result.isPublished()) {
                results.add(result);
            }
        }
        
        return results;
    }
    
    /**
     * Get result for specific exam and student
     */
    public static Result getStudentExamResult(String studentId, String examId) {
        List<String> lines = FileHandler.readFile(RESULTS_FILE);
        
        for (String line : lines) {
            Result result = Result.fromFileString(line);
            if (result.getStudentId().equals(studentId) && 
                result.getExamId().equals(examId)) {
                return result;
            }
        }
        
        return null;
    }
    
    /**
     * Publish result (Admin/Lecturer function, but keeping it for completeness)
     */
    public static boolean publishResult(String resultId) {
        List<String> lines = FileHandler.readFile(RESULTS_FILE);
        
        for (String line : lines) {
            Result result = Result.fromFileString(line);
            if (result.getResultId().equals(resultId)) {
                result.setPublished(true);
                return FileHandler.updateLine(RESULTS_FILE, line, result.toFileString());
            }
        }
        
        return false;
    }
    
    /**
     * Get detailed result with correct answers
     */
    public static Map<String, Object> getDetailedResult(String resultId) {
        Map<String, Object> detailedResult = new HashMap<>();
        
        Result result = getResult(resultId);
        if (result == null) {
            return detailedResult;
        }
        
        Exam exam = ExamService.getExamWithQuestions(result.getExamId());
        if (exam == null) {
            return detailedResult;
        }
        
        detailedResult.put("result", result);
        detailedResult.put("exam", exam);
        
        // Create question-by-question breakdown
        List<Map<String, Object>> questionBreakdown = new ArrayList<>();
        
        for (Question question : exam.getQuestions()) {
            Map<String, Object> qInfo = new HashMap<>();
            qInfo.put("question", question);
            
            String studentAnswer = result.getStudentAnswers().get(question.getQuestionId());
            qInfo.put("studentAnswer", studentAnswer);
            qInfo.put("correctAnswer", question.getCorrectAnswer());
            
            boolean isCorrect = question.checkAnswer(studentAnswer);
            qInfo.put("isCorrect", isCorrect);
            qInfo.put("marksAwarded", isCorrect ? question.getMarks() : 0);
            
            questionBreakdown.add(qInfo);
        }
        
        detailedResult.put("questionBreakdown", questionBreakdown);
        
        return detailedResult;
    }
    
    /**
     * Get result statistics
     */
    public static Map<String, Object> getResultStatistics(String studentId) {
        Map<String, Object> stats = new HashMap<>();
        
        List<Result> results = getPublishedStudentResults(studentId);
        
        if (results.isEmpty()) {
            stats.put("totalExams", 0);
            stats.put("averageScore", 0.0);
            stats.put("averagePercentage", 0.0);
            stats.put("highestScore", 0);
            stats.put("lowestScore", 0);
            return stats;
        }
        
        int totalExams = results.size();
        int totalScore = 0;
        int totalMarks = 0;
        int highest = Integer.MIN_VALUE;
        int lowest = Integer.MAX_VALUE;
        
        for (Result result : results) {
            int score = result.getScore();
            totalScore += score;
            totalMarks += result.getTotalMarks();
            
            if (score > highest) highest = score;
            if (score < lowest) lowest = score;
        }
        
        double averageScore = (double) totalScore / totalExams;
        double averagePercentage = totalMarks > 0 ? (totalScore * 100.0) / totalMarks : 0;
        
        stats.put("totalExams", totalExams);
        stats.put("averageScore", averageScore);
        stats.put("averagePercentage", averagePercentage);
        stats.put("highestScore", highest);
        stats.put("lowestScore", lowest);
        stats.put("totalScore", totalScore);
        stats.put("totalMarks", totalMarks);
        
        return stats;
    }
    
    /**
     * Submit re-correction request
     */
    public static boolean submitRecorrectionRequest(String studentId, String examId, 
                                                    String resultId, String reason) {
        // Validate result exists
        Result result = getResult(resultId);
        if (result == null || !result.getStudentId().equals(studentId)) {
            System.out.println("Invalid result");
            return false;
        }
        
        // Check if result is published
        if (!result.isPublished()) {
            System.out.println("Cannot request re-correction for unpublished result");
            return false;
        }
        
        // Check if already requested
        if (hasRecorrectionRequest(studentId, resultId)) {
            System.out.println("Re-correction request already exists");
            return false;
        }
        
        // Create request
        String requestId = FileHandler.generateId(RECORRECTION_FILE, "RR");
        RecorrectionRequest request = new RecorrectionRequest(
            requestId, studentId, examId, resultId, reason
        );
        
        boolean saved = FileHandler.appendToFile(RECORRECTION_FILE, request.toFileString());
        
        if (saved) {
            System.out.println("Re-correction request submitted successfully!");
        }
        
        return saved;
    }
    
    /**
     * Check if re-correction request exists
     */
    public static boolean hasRecorrectionRequest(String studentId, String resultId) {
        List<String> lines = FileHandler.readFile(RECORRECTION_FILE);
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 4 && 
                parts[1].equals(studentId) && 
                parts[3].equals(resultId)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get all re-correction requests for a student
     */
    public static List<RecorrectionRequest> getStudentRecorrectionRequests(String studentId) {
        List<RecorrectionRequest> requests = new ArrayList<>();
        List<String> lines = FileHandler.readFile(RECORRECTION_FILE);
        
        for (String line : lines) {
            RecorrectionRequest request = RecorrectionRequest.fromFileString(line);
            if (request.getStudentId().equals(studentId)) {
                requests.add(request);
            }
        }
        
        return requests;
    }
    
    /**
     * Get re-correction request by ID
     */
    public static RecorrectionRequest getRecorrectionRequest(String requestId) {
        List<String> lines = FileHandler.readFile(RECORRECTION_FILE);
        
        for (String line : lines) {
            RecorrectionRequest request = RecorrectionRequest.fromFileString(line);
            if (request.getRequestId().equals(requestId)) {
                return request;
            }
        }
        
        return null;
    }
    
    /**
     * Submit feedback for exam
     */
    public static boolean submitFeedback(String studentId, String examId, 
                                        int difficultyRating, int clarityRating, 
                                        String comments) {
        // Validate ratings
        if (difficultyRating < 1 || difficultyRating > 5 || 
            clarityRating < 1 || clarityRating > 5) {
            System.out.println("Ratings must be between 1 and 5");
            return false;
        }
        
        // Check if student took the exam
        if (!ExamService.hasStudentAttemptedExam(studentId, examId)) {
            System.out.println("Cannot submit feedback for exam not taken");
            return false;
        }
        
        // Check if feedback already submitted
        if (hasFeedback(studentId, examId)) {
            System.out.println("Feedback already submitted for this exam");
            return false;
        }
        
        // Create feedback
        String feedbackId = FileHandler.generateId(FEEDBACK_FILE, "F");
        Feedback feedback = new Feedback(
            feedbackId, studentId, examId, 
            difficultyRating, clarityRating, comments
        );
        
        boolean saved = FileHandler.appendToFile(FEEDBACK_FILE, feedback.toFileString());
        
        if (saved) {
            System.out.println("Feedback submitted successfully!");
        }
        
        return saved;
    }
    
    /**
     * Check if feedback exists
     */
    public static boolean hasFeedback(String studentId, String examId) {
        List<String> lines = FileHandler.readFile(FEEDBACK_FILE);
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 3 && 
                parts[1].equals(studentId) && 
                parts[2].equals(examId)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get student's feedback submissions
     */
    public static List<Feedback> getStudentFeedback(String studentId) {
        List<Feedback> feedbackList = new ArrayList<>();
        List<String> lines = FileHandler.readFile(FEEDBACK_FILE);
        
        for (String line : lines) {
            Feedback feedback = Feedback.fromFileString(line);
            if (feedback.getStudentId().equals(studentId)) {
                feedbackList.add(feedback);
            }
        }
        
        return feedbackList;
    }
    
    /**
     * Compare student performance across exams
     */
    public static List<Map<String, Object>> getPerformanceComparison(String studentId) {
        List<Map<String, Object>> performance = new ArrayList<>();
        List<Result> results = getPublishedStudentResults(studentId);
        
        for (Result result : results) {
            Map<String, Object> examPerformance = new HashMap<>();
            
            Exam exam = ExamService.getExam(result.getExamId());
            if (exam != null) {
                examPerformance.put("examTitle", exam.getTitle());
                examPerformance.put("subjectCode", exam.getSubjectCode());
            }
            
            examPerformance.put("score", result.getScore());
            examPerformance.put("totalMarks", result.getTotalMarks());
            examPerformance.put("percentage", result.getPercentage());
            examPerformance.put("grade", result.getGrade());
            examPerformance.put("submissionTime", result.getSubmissionTime());
            
            performance.add(examPerformance);
        }
        
        return performance;
    }
}