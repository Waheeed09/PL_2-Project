package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import models.Exam;
import models.Student;
import models.User;
import services.StudentService;

public class StudentDashboard extends JFrame {

    private Student student;
    private StudentService studentService;

    private DefaultListModel<String> examListModel;
    private JList<String> examList;
    private List<Exam> exams;

    // ======= الكونستركتور المعدل =======
    public StudentDashboard(User user) {
        if (!(user instanceof Student)) {
            throw new IllegalArgumentException("User must be a Student");
        }
        this.student = (Student) user;
        this.studentService = new StudentService();
        this.exams = studentService.loadExams();

        setTitle("Student Dashboard - " + student.getName());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        examListModel = new DefaultListModel<>();
        examList = new JList<>(examListModel);
        for (Exam e : exams) {
            examListModel.addElement(e.getExamId() + " - " + e.getTitle());
        }

        JScrollPane scrollPane = new JScrollPane(examList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));

        JButton btnTakeExam = new JButton("Take Exam");
        JButton btnViewResults = new JButton("View Results");

        buttonPanel.add(btnTakeExam);
        buttonPanel.add(btnViewResults);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        btnTakeExam.addActionListener(e -> takeExam());
        btnViewResults.addActionListener(e -> viewResults());
    }

    private void takeExam() {
        int index = examList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Select an exam to take.");
            return;
        }
        Exam exam = exams.get(index);
        studentService.takeExam(student.getId(), exam.getExamId());
    }

    private void viewResults() {
        JOptionPane.showMessageDialog(this, "Feature not implemented yet.");
    }
}
