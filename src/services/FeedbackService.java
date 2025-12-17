package services;

import java.util.List;
import models.Feedback;

public class FeedbackService {

    public static void addFeedback(int studentId, int examId, String message) {
        List<Feedback> all = FileManager.loadFeedback();
        Feedback f = new Feedback(String.valueOf(studentId), String.valueOf(examId), message);
        all.add(f);
        FileManager.saveFeedback(all);
        System.out.println("Feedback saved successfully!");
    }

    public static void listFeedbacks() {
        System.out.println("\n--- All Students Feedbacks ---");
        List<Feedback> all = FileManager.loadFeedback();
        if (all.isEmpty()) {
            System.out.println("No feedbacks found yet.");
            return;
        }
        for (Feedback f : all) {
            System.out.println("Student ID: " + f.getStudentId() + " | Exam ID: " + f.getExamId() + " | Message: " + f.getText());
        }
    }
}