package services;

import models.Student;
import models.Exam;
import models.Question;

import java.io.*;
import java.util.ArrayList;

public class StudentService {

private final String STUDENTS_FILE = "data/students.txt";
private final String EXAMS_FILE = "data/exams.txt";
private final String QUESTIONS_FILE = "data/questions.txt";
private final String SUBMISSIONS_FILE = "data/submissions.txt";


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

    // ------------------------------------------
    // تسجيل دخول الطالب
    // ------------------------------------------
    public Student login(String email, String password) {
        for (Student s : loadStudents()) {
            if (s.getEmail().equals(email) && s.getPassword().equals(password)) {
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
                exams.add(new Exam(p[0], p[1]));
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
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty() || line.startsWith("examId")) continue;
            String[] p = line.split(",", 3); // examId,text,correct
            if (p[0].equals(examId)) {
                questions.add(new Question(p[0], p[1], p[2]));
            }
        }
    } catch (Exception e) {
        System.out.println("Error loading questions");
    }
    return questions;
}


    // ------------------------------------------
    // حفظ إجابات الطالب
    // ------------------------------------------
    public void saveSubmission(int studentId, String examId, ArrayList<String> answers) {
        String joined = String.join("|", answers);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SUBMISSIONS_FILE, true))) {
            bw.write(studentId + "," + examId + "," + joined);
            bw.newLine();
        } catch (Exception e) {
            System.out.println("Error saving submission");
        }
    }

    // ------------------------------------------
    // التحقق إذا الطالب دخل الامتحان قبل كده
    // ------------------------------------------
    public boolean hasTakenExam(int studentId, String examId) {
        try (BufferedReader br = new BufferedReader(new FileReader(SUBMISSIONS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (Integer.parseInt(p[0]) == studentId && p[1].equals(examId)) {
                    return true;
                }
            }
        } catch (Exception e) { }
        return false;
    }
}
