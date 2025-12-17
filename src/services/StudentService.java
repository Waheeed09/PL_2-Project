package services;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import models.Exam;
import models.Question;
import models.Student;
import models.Subject;

public class StudentService {

    private final String STUDENTS_FILE = "data/students.txt";
    private final String SUBJECTS_FILE = "data/subjects.txt";
    private final String STUDENT_SUBJECTS_FILE = "data/student_subjects.txt";
    private final String EXAMS_FILE = "data/exams.txt";
    private final String QUESTIONS_FILE = "data/questions.txt";
    private final String SUBMISSIONS_FILE = "data/submissions.txt";
    private final String RESULTS_FILE = "data/results.txt";

    // تخزين مؤقت لإجابات الطلاب قبل الحفظ النهائي
    private Map<Integer, Map<Integer, String>> tempAnswers = new HashMap<>();

    // ------------------------------------------
    // تحميل كل الطلاب
    // ------------------------------------------
    public ArrayList<Student> loadStudents() {
        ArrayList<Student> students = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(STUDENTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("id")) continue;
                String[] p = line.split(",");
                int id = Integer.parseInt(p[0]);
                String name = p[1];
                String email = p[2];
                String password = p[3];
                students.add(new Student(id, name, email, password));
            }
        } catch (Exception e) {
            System.out.println("Error loading students");
        }
        return students;
    }

    // Get student results
    public String getStudentResults(int studentId) {
        StringBuilder results = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(RESULTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", 4);
                if (parts.length < 3) continue;

                int subStudentId = Integer.parseInt(parts[0].trim());
                if (subStudentId != studentId) continue;

                String subjectId = parts[1].trim();
                String grade = parts[2].trim();
                String approved = parts.length >= 4 ? parts[3].trim() : "";

                results.append("Subject: ").append(subjectId)
                       .append(", Grade: ").append(grade);
                if (!approved.isEmpty()) {
                    results.append(", Approved: ").append(approved);
                }
                results.append("\n");
            }
        } catch (Exception e) {
            System.out.println("Error loading student results: " + e.getMessage());
        }
        return results.toString();
    }

    // Get registered subjects for a student
    public String getRegisteredSubjects(int studentId) {
        // This is a simplified version - you might need to adjust based on your data structure
        // Assuming subjects are stored in a file with student_id,subject_id,subject_name
        StringBuilder subjects = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_SUBJECTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 3 && Integer.parseInt(parts[0]) == studentId) {
                    subjects.append("-").append(parts[2]).append("\n");
                }
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist yet - no subjects registered
            return "";
        } catch (Exception e) {
            System.out.println("Error loading student subjects: " + e.getMessage());
        }
        return subjects.toString();
    }

    public ArrayList<Subject> loadAllSubjects() {
        ArrayList<Subject> subjects = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(SUBJECTS_FILE))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;
                String subjectId = parts[0].trim();
                String subjectName = parts[1].trim();
                if (subjectId.isEmpty() || subjectName.isEmpty()) continue;
                subjects.add(new Subject(subjectId, subjectName));
            }
        } catch (Exception e) {
            System.out.println("Error loading subjects: " + e.getMessage());
        }
        return subjects;
    }

    public boolean registerSubject(int studentId, String subjectId, String subjectName) {
        if (subjectId == null || subjectId.trim().isEmpty()) return false;
        if (subjectName == null) subjectName = "";

        if (isSubjectRegistered(studentId, subjectId.trim())) {
            return false;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(STUDENT_SUBJECTS_FILE, true))) {
            bw.write(studentId + "," + subjectId.trim() + "," + subjectName.trim());
            bw.newLine();
            return true;
        } catch (Exception e) {
            System.out.println("Error registering subject: " + e.getMessage());
            return false;
        }
    }

    private boolean isSubjectRegistered(int studentId, String subjectId) {
        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_SUBJECTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", 3);
                if (parts.length < 2) continue;
                int sid = Integer.parseInt(parts[0].trim());
                String subId = parts[1].trim();
                if (sid == studentId && subId.equalsIgnoreCase(subjectId)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    // ------------------------------------------
    // Display Students
    // ------------------------------------------
    public void displayStudents() {
        ArrayList<Student> students = loadStudents();
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.println("Students:");
        for (Student s : students) {
            System.out.println(s);
        }
    }

    // ------------------------------------------
    // أداء امتحان
    // ------------------------------------------
    public void takeExam(int studentId, String examId) {
        if (hasTakenExam(studentId, examId)) {
            System.out.println("You have already taken this exam.");
            return;
        }

        ArrayList<Question> questions = loadQuestions(examId);
        if (questions.isEmpty()) {
            System.out.println("No questions found for this exam.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Starting exam: " + examId);
        for (Question q : questions) {
            System.out.println(q.getText());
            if (q.getOptions() != null && !q.getOptions().isEmpty()) {
                for (int i = 0; i < q.getOptions().size(); i++) {
                    System.out.println((i+1) + ". " + q.getOptions().get(i));
                }
            }
            System.out.print("Your answer: ");
            String answer = sc.nextLine();
            // Store temporarily
            tempAnswers.computeIfAbsent(studentId, k -> new HashMap<>()).put(q.getId(), answer);
        }

        // Submit after all questions
        saveSubmission(studentId, examId);
        System.out.println("Exam submitted!");
    }

    // ------------------------------------------
    // الحصول على طالب بالـ ID
    // ------------------------------------------
    public Student getStudentById(int studentId) {
        for (Student s : loadStudents()) {
            if (s.getId() == studentId) {
                return s;
            }
        }
        return null;
    }

    // ------------------------------------------
    // تحميل الامتحانات
    // ------------------------------------------
    public ArrayList<Exam> loadExams() {
        ArrayList<Exam> exams = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(EXAMS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("examId")) continue;
                String[] p = line.split(",");
                if (p.length >= 2) {
                    String examId = p[0];
                    String subject = p[1];
                    // Use subject as title, subjectId, default lecturer and duration
                    exams.add(new Exam(examId, subject, subject, "Default Lecturer", 60));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading exams");
        }
        return exams;
    }

    // ------------------------------------------
    // تحميل الأسئلة لامتحان محدد
    // ------------------------------------------
    public ArrayList<Question> loadQuestions(String examId) {
        ArrayList<Question> questions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(QUESTIONS_FILE))) {
            String line;
            int id = 1; // auto increment id
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("examId")) continue;

                // format: examId,text,correct
                String[] p = line.split(",", 3);
                if (p[0].equals(examId)) {
                    questions.add(new Question(id++, p[1], p[2]));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading questions");
        }
        return questions;
    }

    // ------------------------------------------
    // حفظ إجابة طالب مؤقتًا
    // ------------------------------------------
    public void answerQuestion(int studentId, int questionId, String answer) {
        tempAnswers.putIfAbsent(studentId, new HashMap<>());
        tempAnswers.get(studentId).put(questionId, answer);
    }

    // ------------------------------------------
    // حفظ إجابات الطالب النهائية في الملف
    // ------------------------------------------
    public void saveSubmission(int studentId, String examId) {
        if (!tempAnswers.containsKey(studentId)) return;

        Map<Integer, String> answers = tempAnswers.get(studentId);

        // format: studentId,examId,answer1;answer2;answer3;...
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= answers.size(); i++) {
            if (answers.containsKey(i)) {
                if (sb.length() > 0) sb.append(";");
                sb.append(answers.get(i));
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SUBMISSIONS_FILE, true))) {
            bw.write(studentId + "," + examId + "," + sb.toString());
            bw.newLine();
        } catch (Exception e) {
            System.out.println("Error saving submission");
        }

        // حساب الدرجة وحفظ النتيجة
        String answers_str = sb.toString();
        LecturerService lecturerService = new LecturerService();
        double grade = lecturerService.calculateGradeFromSubmission(examId, answers_str);
        
        // Get subjectId from exam
        String subjectId = examId; // Use examId as subject for now (can be improved)
        ArrayList<Exam> exams = loadExams();
        for (Exam e : exams) {
            if (e.getExamId().equals(examId)) {
                subjectId = e.getSubjectId();
                break;
            }
        }
        
        // Save result to results.txt in correct format: studentId,subjectId,grade,approved
        try (FileWriter fw = new FileWriter("data/results.txt", true)) {
            boolean approved = grade >= 50;
            fw.write(studentId + "," + subjectId + "," + grade + "," + approved + "\n");
        } catch (Exception e) {
            System.out.println("Error saving result: " + e.getMessage());
        }
        
        System.out.println("✓ Grade recorded: " + String.format("%.2f", grade) + "%");

        // مسح مؤقت بعد الحفظ
        tempAnswers.remove(studentId);
    }

    // ------------------------------------------
    // التحقق إذا الطالب دخل الامتحان قبل كده
    // ------------------------------------------
    public boolean hasTakenExam(int studentId, String examId) {
        try (BufferedReader br = new BufferedReader(new FileReader(SUBMISSIONS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", 3);
                if (Integer.parseInt(p[0]) == studentId && p[1].equals(examId)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading submissions");
        }
        return false;
    }
}
