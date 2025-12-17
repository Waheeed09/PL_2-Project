package GUI;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import models.Exam;
import models.Student;
import services.StudentService;

public class StudentDashboard extends JFrame {

    private Student student;
    private StudentService studentService;
    private List<Exam> exams;

    public StudentDashboard(Student student, StudentService studentService) {
        this.student = student;
        this.studentService = studentService;
        this.exams = studentService.loadExams();

        setTitle("Student Dashboard - " + student.getName());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        DefaultListModel<String> examListModel = new DefaultListModel<>();
        JList<String> examList = new JList<>(examListModel);
        for (Exam e : exams) {
            examListModel.addElement(e.getExamId() + " - " + e.getTitle());
        }

        JScrollPane scrollPane = new JScrollPane(examList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 5, 5));

        JButton btnTakeExam = new JButton("Take Exam");
        JButton btnViewResults = new JButton("View Results");
        JButton btnRecorrection = new JButton("Request Recorrection");

        buttonPanel.add(btnTakeExam);
        buttonPanel.add(btnViewResults);
        buttonPanel.add(btnRecorrection);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        btnTakeExam.addActionListener(e -> {
            int index = examList.getSelectedIndex();
            if (index == -1) {
                JOptionPane.showMessageDialog(this, "Select an exam to take.");
                return;
            }
            Exam exam = exams.get(index);
            studentService.takeExam(student.getId(), exam.getExamId());
        });

        btnViewResults.addActionListener(e -> JOptionPane.showMessageDialog(this, "Feature not implemented yet."));

        btnRecorrection.addActionListener(e -> {
            new RecorrectionForm(student);
        });
    }
}
