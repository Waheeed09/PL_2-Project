package services;

import java.io.*;
import java.util.*;
import models.*;

public class FileManager {

    private static final String STUDENTS_FILE = "data/students.txt";
    private static final String LECTURERS_FILE = "data/lecturers.txt";
    private static final String SUBJECTS_FILE = "data/subjects.txt";
    private static final String EXAMS_FILE = "data/exams.txt";
    private static final String QUESTIONS_FILE = "data/questions.txt";
    private static final String SUBMISSIONS_FILE = "data/submissions.txt";
    private static final String RESULTS_FILE = "data/results.txt";
    private static final String FEEDBACK_FILE = "data/feedback.txt";
    private static final String RECORRECTIONS_FILE = "data/recorrections.txt";
    private static final String USERS_FILE = "data/users.txt";

    // ------------------- Students -------------------
    public static void saveStudents(List<Student> students) {
        try (FileWriter fw = new FileWriter(STUDENTS_FILE)) {
            for (Student s : students) {
                fw.write(s.getId() + "," + s.getName() + "," + s.getEmail() + "," + s.getPassword() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving students: " + e.getMessage());
        }
    }

    // ------------------- Users -------------------
    public static void saveUsers(List<User> users) {
        try (FileWriter fw = new FileWriter(USERS_FILE)) {
            for (User u : users) {
                fw.write(u.getId() + "," + u.getName() + "," + u.getEmail() + "," + u.getPassword() + "," + u.getRole() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    // Save users and also split into students/lecturers files for convenience
    public static void saveUsersAndRoles(List<User> users) {
        // Save master users file first
        saveUsers(users);

        // Build student and lecturer lists
        List<Student> students = new ArrayList<>();
        List<Lecturer> lecturers = new ArrayList<>();
        for (User u : users) {
            if (u == null) continue;
            String role = u.getRole() != null ? u.getRole().toLowerCase() : "";
            try {
                if ("student".equals(role)) {
                    students.add(new Student(u.getId(), u.getName(), u.getEmail(), u.getPassword()));
                } else if ("lecturer".equals(role)) {
                    lecturers.add(new Lecturer(u.getId(), u.getName(), u.getEmail(), u.getPassword()));
                }
            } catch (Exception ex) {
                // ignore malformed user
            }
        }

        // Save role-specific files
        saveStudents(students);
        saveLecturers(lecturers);
    }

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue;
                int id = Integer.parseInt(parts[0]);
                users.add(new User(id, parts[1], parts[2], parts[3], parts[4]));
            }
        } catch (IOException e) {
            System.out.println("No users file found. Starting fresh.");
        }
        return users;
    }

    public static List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(STUDENTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;
                students.add(new Student(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3]));
            }
        } catch (IOException e) {
            System.out.println("No students file found. Starting fresh.");
        }
        return students;
    }

    // ------------------- Lecturers -------------------
    public static void saveLecturers(List<Lecturer> lecturers) {
        try (FileWriter fw = new FileWriter(LECTURERS_FILE)) {
            for (Lecturer l : lecturers) {
                fw.write(l.getId() + "," + l.getName() + "," + l.getEmail() + "," + l.getPassword() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving lecturers: " + e.getMessage());
        }
    }

    public static List<Lecturer> loadLecturers() {
        List<Lecturer> lecturers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(LECTURERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;
                lecturers.add(new Lecturer(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3]));
            }
        } catch (IOException e) {
            System.out.println("No lecturers file found. Starting fresh.");
        }
        return lecturers;
    }

    // ------------------- Subjects -------------------
    public static void saveSubjects(List<Subject> subjects) {
        try (FileWriter fw = new FileWriter(SUBJECTS_FILE)) {
            fw.write("subjectId,subjectName\n");
            for (Subject s : subjects) {
                fw.write(s.getSubjectId() + "," + s.getSubjectName() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving subjects: " + e.getMessage());
        }
    }

    public static List<Subject> loadSubjects() {
        List<Subject> subjects = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(SUBJECTS_FILE))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length < 2) continue;
                subjects.add(new Subject(parts[0], parts[1]));
            }
        } catch (IOException e) {
            System.out.println("No subjects file found. Starting fresh.");
        }
        return subjects;
    }

    // ------------------- Exams -------------------
    public static void saveExams(List<Exam> exams) {
        try (FileWriter fw = new FileWriter(EXAMS_FILE)) {
            fw.write("examId,subject\n");
            for (Exam e : exams) {
                fw.write(e.getExamId() + "," + e.getSubjectId() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving exams: " + e.getMessage());
        }
    }

    public static List<Exam> loadExams() {
        List<Exam> exams = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(EXAMS_FILE))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;
                String examId = parts[0];
                String subject = parts[1];
                exams.add(new Exam(examId, subject, subject, "Default Lecturer", 60));
            }
        } catch (IOException e) {
            System.out.println("No exams file found. Starting fresh.");
        }
        return exams;
    }

    // ------------------- Questions -------------------
    public static void saveQuestions(List<Question> questions) {
        try (FileWriter fw = new FileWriter(QUESTIONS_FILE)) {
            for (Question q : questions) {
                fw.write(q.getQuestionId() + "," + q.getExamId() + "," + q.getText() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving questions: " + e.getMessage());
        }
    }

    public static List<Question> loadQuestions() {
        List<Question> questions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(QUESTIONS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length < 3) continue;
                try {
                    int qid = Integer.parseInt(parts[0]);
                    Question q = new Question(qid, parts[2], "");
                    q.setExamId(parts[1]);
                    questions.add(q);
                } catch (NumberFormatException ex) {
                    // skip malformed id
                }
            }
        } catch (IOException e) {
            System.out.println("No questions file found. Starting fresh.");
        }
        return questions;
    }

    // ------------------- Results -------------------
    public static void saveResults(List<Result> results) {
        try (FileWriter fw = new FileWriter(RESULTS_FILE)) {
            for (Result r : results) {
                fw.write(r.getStudentId() + "," + r.getSubjectId() + "," + r.getGrade() + "," + r.isApproved() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving results: " + e.getMessage());
        }
    }

    public static List<Result> loadResults() {
        List<Result> results = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(RESULTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;
                Result r = new Result(parts[0], parts[1], Double.parseDouble(parts[2]));
                r.setApproved(Boolean.parseBoolean(parts[3]));
                results.add(r);
            }
        } catch (IOException e) {
            System.out.println("No results file found. Starting fresh.");
        }
        return results;
    }

    // ------------------- Feedback -------------------
    public static void saveFeedback(List<Feedback> feedbacks) {
        try (FileWriter fw = new FileWriter(FEEDBACK_FILE)) {
            for (Feedback f : feedbacks) {
                fw.write(f.getStudentId() + "," + f.getText() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving feedback: " + e.getMessage());
        }
    }

    public static List<Feedback> loadFeedback() {
        List<Feedback> feedbacks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FEEDBACK_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;
                feedbacks.add(new Feedback(parts[0], parts[1]));
            }
        } catch (IOException e) {
            System.out.println("No feedback file found. Starting fresh.");
        }
        return feedbacks;
    }

    // ------------------- Recorrections -------------------
    public static void saveRecorrections(List<Recorrection> recs) {
        try (FileWriter fw = new FileWriter(RECORRECTIONS_FILE)) {
            for (Recorrection r : recs) {
                fw.write(r.getStudentId() + "," + r.getResultId() + "," + r.getReason() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving recorrections: " + e.getMessage());
        }
    }

    public static List<Recorrection> loadRecorrections() {
        List<Recorrection> recs = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(RECORRECTIONS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length < 3) continue;
                recs.add(new Recorrection(parts[0], parts[1], parts[2]));
            }
        } catch (IOException e) {
            System.out.println("No recorrections file found. Starting fresh.");
        }
        return recs;
    }

    // ------------------- Submissions -------------------
    public static void saveSubmissions(List<Submission> submissions) {
        try (FileWriter fw = new FileWriter(SUBMISSIONS_FILE)) {
            for (Submission s : submissions) {
                fw.write(s.getStudentId() + "," + s.getExamId() + "," + s.getAnswer() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving submissions: " + e.getMessage());
        }
    }

    public static List<Submission> loadSubmissions() {
        List<Submission> subs = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(SUBMISSIONS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length < 3) continue;
                subs.add(new Submission(parts[0], parts[1], parts[2]));
            }
        } catch (IOException e) {
            System.out.println("No submissions file found. Starting fresh.");
        }
        return subs;
    }
}
