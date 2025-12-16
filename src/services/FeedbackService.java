package services;

import java.io.FileWriter;
import java.io.IOException;

public class FeedbackService {

    private static final String FILE_PATH = "feedback.txt";

    public static void addFeedback(int studentId, int examId, String message) {
        try {
            FileWriter writer = new FileWriter(FILE_PATH, true);
            writer.write(studentId + "," + examId + "," + message + "\n");
            writer.close();
            System.out.println("Feedback saved!");
        } catch (IOException e) {
        }}

    public static void listFeedbacks() {
        Iterable<FeedbackService> feedbackList = null;
        

    
    for (FeedbackService f : feedbackList) {
        System.out.println(f.toString());

    }
}}
