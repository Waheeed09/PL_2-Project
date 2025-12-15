package services;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import models.Exam;
import models.Question;
import models.Student;

public class StudentService {

    private final String STUDENTS_FILE = "data/students.txt";
    private final String EXAMS_FILE = "data/exams.txt";
    private final String QUESTIONS_FILE = "data/questions.txt";
    private final String SUBMISSIONS_FILE = "data/submissions.txt";

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
                String[] p = line.split(",", 2);
                exams.add(new Exam(p[0], p[1],p[2],p[3], Integer.parseInt(p[4])));
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

                // format: examId,questionId,text,correct
                String[] p = line.split(",", 4);
                if (p[0].equals(examId)) {
                    int questionId = Integer.parseInt(p[1]);
                    questions.add(new Question(questionId, p[2], p[3]));
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

        // format: studentId,examId,questionId1|answer1;questionId2|answer2;...
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, String> entry : answers.entrySet()) {
            sb.append(entry.getKey()).append("|").append(entry.getValue()).append(";");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SUBMISSIONS_FILE, true))) {
            bw.write(studentId + "," + examId + "," + sb.toString());
            bw.newLine();
        } catch (Exception e) {
            System.out.println("Error saving submission");
        }

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
