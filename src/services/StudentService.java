package services;

import java.util.ArrayList;
import models.Exam;
import models.Question;

public class StudentService {

    // تخزين بيانات الامتحانات مؤقتًا
    private ArrayList<Exam> exams = new ArrayList<>();
    private ArrayList<Question> questions = new ArrayList<>();

    public StudentService() {
        // بيانات dummy للامتحانات
        exams.add(new Exam("E001", "Math", 60));
        exams.add(new Exam("E002", "Physics", 45));

        // بيانات dummy للأسئلة
        questions.add(new Question("What is 2+2?", "4"));
        questions.add(new Question("What is gravity?", "Force"));
    }

    // تحميل الامتحانات
    public ArrayList<Exam> loadExams() {
        return exams;
    }

    // تحميل أسئلة امتحان معين (نسخة بسيطة)
    public ArrayList<Question> loadQuestions(String examId) {
        // في النسخة البسيطة هنعيد كل الأسئلة لجميع الامتحانات
        return questions;
    }

    // حفظ إجابات الطالب (نسخة بسيطة)
    public void saveSubmission(String studentId, String examId, ArrayList<String> answers) {
        System.out.println("Student " + studentId + " submitted exam " + examId);
        System.out.println("Answers: " + answers);
    }
}
