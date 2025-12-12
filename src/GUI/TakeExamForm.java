package GUI;

import javax.swing.*;
import java.util.ArrayList;
import models.Exam;
import models.Question;
import services.StudentService;

public class TakeExamForm extends JFrame {

    private int studentId;
    private StudentService studentService = new StudentService();

    public TakeExamForm(int studentId) {
        this.studentId = studentId;

        setTitle("Take Exam");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        JButton btn = new JButton("Show Exams");

        btn.setBounds(120, 10, 150, 30);
        area.setBounds(50, 50, 300, 300);

        add(btn);
        add(area);

        btn.addActionListener(e -> {
            ArrayList<Exam> exams = studentService.loadExams();
            if (exams.isEmpty()) {
                area.setText("No exams available!");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (Exam ex : exams) {
                sb.append(ex.examId).append(" - ").append(ex.subjectName).append("\n");
            }
            area.setText(sb.toString());

            String chosen = JOptionPane.showInputDialog(this, "Enter exam ID from above list:");
            if (chosen != null) {
                if (studentService.hasTakenExam(studentId, chosen)) {
                    JOptionPane.showMessageDialog(this, "You have already submitted this exam!");
                } else {
                    startExam(chosen);
                }
            }
        });

        setVisible(true);
    }

    private void startExam(String examId) {
        ArrayList<Question> questions = studentService.loadQuestions(examId);
        ArrayList<String> answers = new ArrayList<>();

        for (Question q : questions) {
            String ans = JOptionPane.showInputDialog(this, "Question:\n" + q.text);
            answers.add(ans);
        }

        studentService.saveSubmission(studentId, examId, answers);
        JOptionPane.showMessageDialog(this, "Exam submitted!");
    }
}
