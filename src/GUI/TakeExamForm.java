package GUI;


import javax.swing.*;
import java.util.ArrayList;
import models.Exam;
import models.Question;
import services.StudentService;

public class TakeExamForm extends JFrame {

    private String studentId;
    private StudentService studentService = new StudentService();

    public TakeExamForm(String studentId) {
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
            StringBuilder sb = new StringBuilder();

            for (Exam ex : exams) {
                sb.append(ex.examId + " - " + ex.subjectName + "\n");
            }

            area.setText(sb.toString());

            String chosen = JOptionPane.showInputDialog("Enter exam ID:");
            if (chosen != null) {
                startExam(chosen);
            }
        });

        setVisible(true);
    }

    private void startExam(String examId) {
        ArrayList<Question> questions = studentService.loadQuestions(examId);
        ArrayList<String> answers = new ArrayList<>();

        for (Question q : questions) {
            String ans = JOptionPane.showInputDialog("Question:\n" + q.text);
            answers.add(ans);
        }

        studentService.saveSubmission(studentId, examId, answers);
        JOptionPane.showMessageDialog(this, "Exam submitted!");
    }
}

