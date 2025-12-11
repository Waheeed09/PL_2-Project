package services;

public class FeedbackService {

private static java.util.ArrayList<FeedbackService> feedbackList = new java.util.ArrayList<>();

public static void addFeedback(FeedbackService f) {
    feedbackList.add(f);
    System.out.println("Feedback added!");
}

public static void listFeedbacks() {
    if (feedbackList.isEmpty()) {
        System.out.println("No feedback added yet.");
        return;
    }

    for (FeedbackService f : feedbackList) {
        System.out.println(f.toString());
    }
}}
