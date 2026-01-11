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
        String bgPath = "D:\\Uni_Bedo\\Pl2\\Project_pl2\\PL_2-Project\\resources\\WhatsApp Image 2026-01-11 at 4.44.40 PM.jpeg";
        Image bg = UITheme.loadBackgroundImageAbsolute(bgPath);
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        background.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        DefaultListModel<String> examListModel = new DefaultListModel<>();
        JList<String> examList = new JList<>(examListModel);
        examList.setFont(UITheme.FONT_BODY);
        for (Exam e : exams) {
            examListModel.addElement(e.getExamId() + " - " + e.getTitle());
        }

        JScrollPane scrollPane = new JScrollPane(examList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 8, 8));
        buttonPanel.setOpaque(false);

        JButton btnTakeExam = new JButton("Take Exam"); UITheme.styleButton(btnTakeExam);
        JButton btnViewResults = new JButton("View Results"); UITheme.styleButton(btnViewResults);
        JButton btnRecorrection = new JButton("Request Recorrection"); UITheme.styleButton(btnRecorrection);

        buttonPanel.add(btnTakeExam);
        buttonPanel.add(btnViewResults);
        buttonPanel.add(btnRecorrection);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        background.add(mainPanel, BorderLayout.CENTER);
        setContentPane(background);

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
