package services;

import models.Exam;
import models.Lecturer;
import models.Question;
import models.Student;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.io.File;
import java.io.BufferedReader;
import java.util.*;


public class LecturerService {

    // Validate
    private void validateLecturer(Lecturer lecturer) {
        if (lecturer == null || !"lecturer".equals(lecturer.getRole())) {
            throw new IllegalStateException("Unauthorized action");
        }
    }

//    Create
    public void createExam(Lecturer lecturer, String examId, String title) {
        validateLecturer(lecturer);

        if (examId == null || title == null) {
            throw new IllegalArgumentException("Exam ID and Title cannot be null");
        }

        try (FileWriter writer = new FileWriter("exams.txt", true)) { // true = append
            writer.write(examId + "," + title);
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            throw new RuntimeException("Error saving exam");
        }

        System.out.println("Exam created by lecturer: " + lecturer.getName());
    }


// Modify
    public void modifyExam(Lecturer lecturer, String oldExamId, String newExamId, String newTitle) {
        validateLecturer(lecturer);

        if (oldExamId == null || newExamId == null || newTitle == null) {
            throw new IllegalArgumentException("Exam ID and Title cannot be null");
        }

        File file = new File("exams.txt");
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts[0].equals(oldExamId)) {
                    lines.add(newExamId + "," + newTitle);
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading exams file");
        }

        try (FileWriter writer = new FileWriter(file, false)) {
            for (String l : lines) {
                writer.write(l);
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error updating exams file");
        }

        System.out.println("Exam modified by lecturer: " + lecturer.getName());
    }

// AddQ
    public void addQuestion(Lecturer lecturer, Exam exam, Question question) {
        validateLecturer(lecturer);

        if (exam == null || question == null) {
            throw new IllegalArgumentException("Exam or Question cannot be null");
        }

        exam.getQuestions().add(question);
        System.out.println("Question added by lecturer");
    }

//  Calc_Grade
    public int calculateGrade(Exam exam, Student student) {
        if (exam == null || student == null) {
            throw new IllegalArgumentException("Exam or Student cannot be null");
        }

        int score = 0;
        for (Question q : exam.getQuestions()) {
            if (q.isAnsweredCorrectlyBy(student)) {
                score++;
            }
        }
        return score;
    }
// Gen_report
    public void generateClassReport(Exam exam, List<Student> students) {
        if (exam == null || students == null) {
            throw new IllegalArgumentException("Exam or Students cannot be null");
        }

        System.out.println("Class Report for Exam: " + exam.getTitle());

        for (Student student : students) {
            int grade = calculateGrade(exam, student);
            System.out.println(student.getName() + " -> " + grade);
        }
    }
}
