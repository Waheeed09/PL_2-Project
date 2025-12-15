package services;

import models.Exam;
import models.Question;
import models.Result;
import java.util.List;
import java.util.ArrayList;


public class ExamService {

    private List<Exam> exams;

    public ExamService() {
        this.exams = new ArrayList<>();
    }

    // ================= Create Exam =================
    public Exam createExam(String examId, String title, String subjectId,
                           String lecturerId, int durationMinutes) {
        Exam exam = new Exam(examId, title, subjectId, lecturerId, durationMinutes);
        exams.add(exam);
        return exam;
    }

    // ================= Get All Exams =================
    public List<Exam> getAllExams() {
        return exams;
    }

    // ================= Find Exam by ID =================
    public Exam getExamById(String examId) {
        for (Exam e : exams) {
            if (e.getExamId().equals(examId)) {
                return e;
            }
        }
        return null;
    }

    // ================= Add Question to Exam =================
    public boolean addQuestionToExam(String examId, Question question) {
        Exam exam = getExamById(examId);
        if (exam != null && question != null) {
            exam.addQuestion(question);
            return true;
        }
        return false;
    }

    // ================= Add Result to Exam =================
    public boolean addResultToExam(String examId, Result result) {
        Exam exam = getExamById(examId);
        if (exam != null && result != null) {
            exam.addResult(result);
            return true;
        }
        return false;
    }

    // ================= Update Question in Exam =================
    public boolean updateQuestionInExam(String examId, int questionId, Question newQuestion) {
        Exam exam = getExamById(examId);
        if (exam != null && newQuestion != null) {
            exam.updateQuestion(questionId, newQuestion);
            return true;
        }
        return false;
    }

    // ================= Display Exams & Results =================
    public void displayExams() {
        for (Exam e : exams) {
            System.out.println("Exam: " + e.getTitle() + " | Subject: " + e.getSubjectId() + " | Total Grade: " + e.getTotalGrade());
            System.out.println("Questions:");
            for (Question q : e.getQuestions()) {
                System.out.println("- " + q.getText() + " (" + q.getPoints() + " pts)");
            }
            System.out.println("Results:");
            for (Result r : e.getResults()) {
                System.out.println("- " + r);
            }
            System.out.println("-----------------------------------");
        }
    }
}

