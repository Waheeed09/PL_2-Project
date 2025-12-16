package services;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FeedbackService {

    private static final String FILE_PATH = "data/feedback.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static void addFeedback(int studentId, int examId, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String safeMessage = message == null ? "" : message.replace("|", "/");

        Path path = Paths.get(FILE_PATH);
        try {
            if (path.getParent() != null && Files.notExists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
        } catch (IOException e) {
            System.err.println("Unable to create data directory: " + e.getMessage());
        }

        int id = 1;
        try {
            if (Files.exists(path)) {
                long lines = Files.lines(path).filter(l -> !l.trim().isEmpty() && !l.startsWith("id|")).count();
                id = (int) lines + 1;
            }
        } catch (IOException e) {
            System.err.println("Failed to read feedback file: " + e.getMessage());
        }

        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.write(String.format("%d|%d|%d|%s|%s%n", id, studentId, examId, timestamp, safeMessage));
            System.out.println("Feedback saved!");
        } catch (IOException e) {
            System.err.println("Failed to save feedback: " + e.getMessage());
        }
    }

    public static void listFeedbacks() {
        Path path = Paths.get(FILE_PATH);
        if (Files.notExists(path)) {
            System.out.println("No feedback file found: " + FILE_PATH);
            return;
        }

        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                if (line == null) continue;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("id|")) continue;
                String[] parts = line.split("\\|", 5);
                if (parts.length < 5) {
                    System.out.println("Malformed feedback: " + line);
                    continue;
                }
                System.out.printf("[%s] student:%s exam:%s time:%s - %s%n", parts[0], parts[1], parts[2], parts[3], parts[4]);
            }
        } catch (IOException e) {
            System.err.println("Failed to read feedbacks: " + e.getMessage());
        }
    }

}
