package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import models.Exam;
import models.Lecturer;
import models.Question;
import models.Student;


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

        try (FileWriter writer = new FileWriter("data/exams.txt", true)) { // true = append
            writer.write(examId + "," + title);
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            throw new RuntimeException("Error saving exam");
        }

        System.out.println("Exam created by lecturer: " + lecturer.getName());
    }

    // Create exam for a specific subject
    public void createExamForSubject(Lecturer lecturer, Scanner sc) {
        validateLecturer(lecturer);
        
        List<String> subjects = FileManager.loadSubjects().stream()
                .map(s -> s.getSubjectId() + " - " + s.getSubjectName())
                .toList();
        
        if (subjects.isEmpty()) {
            System.out.println("No subjects available. Admin must create subjects first.");
            return;
        }
        
        System.out.println("\n--- Available Subjects ---");
        for (int i = 0; i < subjects.size(); i++) {
            System.out.println((i + 1) + ". " + subjects.get(i));
        }
        
        System.out.print("Select subject (number): ");
        int choice = Integer.parseInt(sc.nextLine()) - 1;
        if (choice < 0 || choice >= subjects.size()) {
            System.out.println("Invalid choice");
            return;
        }
        
        String subjectId = subjects.get(choice).split(" - ")[0];
        
        System.out.print("Enter Exam ID: ");
        String examId = sc.nextLine();
        System.out.print("Enter Exam Title: ");
        String title = sc.nextLine();
        
        // Save exam with subjectId
        try (FileWriter writer = new FileWriter("data/exams.txt", true)) {
            writer.write(examId + "," + subjectId + "\n");
        } catch (IOException e) {
            System.out.println("Error saving exam");
            return;
        }
        
        System.out.println("✓ Exam created for subject: " + subjectId);
        
        // Add questions
        System.out.println("\n--- Add Questions to Exam ---");
        System.out.print("How many questions? ");
        int numQuestions = Integer.parseInt(sc.nextLine());
        
        try (FileWriter writer = new FileWriter("data/questions.txt", true)) {
            for (int i = 0; i < numQuestions; i++) {
                System.out.println("\n[Question " + (i+1) + "]");
                System.out.print("Question text: ");
                String questionText = sc.nextLine();
                System.out.print("Correct answer: ");
                String correctAnswer = sc.nextLine();
                
                writer.write(examId + "," + questionText + "," + correctAnswer + "\n");
                System.out.println("✓ Question added");
            }
        } catch (IOException e) {
            System.out.println("Error saving questions: " + e.getMessage());
        }
    }

    // Create exam with questions (old method - keeps backward compatibility)
    public void createExamWithQuestions(Lecturer lecturer, String examId, String title, Scanner sc) {
        validateLecturer(lecturer);
        createExam(lecturer, examId, title);
        
        System.out.println("\n--- Add Questions to Exam ---");
        System.out.print("How many questions? ");
        int numQuestions = Integer.parseInt(sc.nextLine());
        
        
        try (FileWriter writer = new FileWriter("data/questions.txt", true)) {
            for (int i = 0; i < numQuestions; i++) {
                System.out.println("\n[Question " + (i+1) + "]");
                System.out.print("Question text: ");
                String questionText = sc.nextLine();
                System.out.print("Correct answer: ");
                String correctAnswer = sc.nextLine();
                
                writer.write(examId + "," + questionText + "," + correctAnswer);
                writer.write(System.lineSeparator());
                System.out.println("✓ Question added");
            }
        } catch (IOException e) {
            System.out.println("Error saving questions: " + e.getMessage());
        }
    }


// Modify
    public void modifyExam(Lecturer lecturer, String oldExamId, String newExamId, String newTitle) {
        validateLecturer(lecturer);

        if (oldExamId == null || newExamId == null || newTitle == null) {
            throw new IllegalArgumentException("Exam ID and Title cannot be null");
        }

        File file = new File("data/exams.txt");
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
// Delete Exam
    public void deleteExam(Lecturer lecturer, String examId) {
        validateLecturer(lecturer);

        List<Exam> exams = FileManager.loadExams();
        boolean found = false;
        for (int i = 0; i < exams.size(); i++) {
            Exam exam = exams.get(i);
            if (exam.getExamId().equals(examId)) {
                exams.remove(i);
                found = true;
                break;
            }
        }
        if (found) {
            FileManager.saveExams(exams);
            System.out.println("Exam deleted successfully: " + examId);
        } else {
            System.out.println("Exam not found.");
        }
    }

// View Exams
    public void viewExams(Lecturer lecturer) {
        validateLecturer(lecturer);

        List<Exam> exams = FileManager.loadExams();
        if (exams.isEmpty()) {
            System.out.println("No exams found.");
        } else {
            System.out.println("Exams:");
            for (Exam exam : exams) {
                System.out.println("ID: " + exam.getExamId() + ", Subject: " + exam.getSubjectId());
            }
        }
    }

    // Grade calculator: compare answers with correct ones
    public double calculateGradeFromSubmission(String examId, String answers) {
        List<Question> questions = FileManager.loadQuestions();
        List<Question> examQuestions = new ArrayList<>();
        for (Question q : questions) {
            if (q.getExamId().equals(examId)) {
                examQuestions.add(q);
            }
        }

        if (examQuestions.isEmpty()) return 0;

        String[] answerArray = answers.split(";");
        int correct = 0;
        for (int i = 0; i < examQuestions.size() && i < answerArray.length; i++) {
            String studentAnswer = answerArray[i].trim();
            if (examQuestions.get(i).isCorrectAnswer(studentAnswer)) {
                correct++;
            }
        }
        return (correct * 100.0) / examQuestions.size();
    }

    
}
