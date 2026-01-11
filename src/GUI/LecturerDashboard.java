package GUI;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.Exam;
import models.Lecturer;
import models.Question;
import services.LecturerService;

public class LecturerDashboard extends JFrame {

    private Lecturer lecturer;
    private LecturerService lecturerService;
    private List<Exam> exams;

    public LecturerDashboard(Lecturer lecturer, LecturerService lecturerService) {
        this.lecturer = lecturer;
        this.lecturerService = lecturerService;
        this.exams = new ArrayList<>();

        setTitle("Lecturer Dashboard - " + lecturer.getName());
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
        JScrollPane scrollPane = new JScrollPane(examList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 8, 8));
        buttonPanel.setOpaque(false);
        JButton btnCreate = new JButton("Create Exam"); UITheme.styleButton(btnCreate);
        JButton btnModify = new JButton("Modify Exam"); UITheme.styleButton(btnModify);
        JButton btnDelete = new JButton("Delete Exam"); UITheme.styleButton(btnDelete);
        JButton btnView = new JButton("View Exams"); UITheme.styleButton(btnView);
        JButton btnAddQ = new JButton("Add Question"); UITheme.styleButton(btnAddQ);

        buttonPanel.add(btnCreate);
        buttonPanel.add(btnModify);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnView);
        buttonPanel.add(btnAddQ);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        background.add(mainPanel, BorderLayout.CENTER);
        setContentPane(background);

        btnCreate.addActionListener(e -> {
            JTextField idField = new JTextField();
            JTextField titleField = new JTextField();
            Object[] message = {"Exam ID:", idField, "Title:", titleField};
            int option = JOptionPane.showConfirmDialog(this, message, "Create Exam", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String id = idField.getText().trim();
                String title = titleField.getText().trim();
                if (!id.isEmpty() && !title.isEmpty()) {
                    Exam exam = new Exam(id, title, "", String.valueOf(lecturer.getId()), 60);
                    exams.add(exam);
                    examListModel.addElement(id + " - " + title);
                    lecturerService.createExam(lecturer, id, title);
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter both ID and Title.");
                }
            }
        });

        btnModify.addActionListener(e -> {
            int index = examList.getSelectedIndex();
            if (index == -1) {
                JOptionPane.showMessageDialog(this, "Select an exam to modify.");
                return;
            }
            Exam exam = exams.get(index);
            JTextField idField = new JTextField(exam.getExamId());
            JTextField titleField = new JTextField(exam.getTitle());
            Object[] message = {"Exam ID:", idField, "Title:", titleField};
            int option = JOptionPane.showConfirmDialog(this, message, "Modify Exam", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String newId = idField.getText().trim();
                String newTitle = titleField.getText().trim();
                if (!newId.isEmpty() && !newTitle.isEmpty()) {
                    exams.set(index, new Exam(newId, newTitle, "", String.valueOf(lecturer.getId()), 60));
                    examListModel.set(index, newId + " - " + newTitle);
                    lecturerService.modifyExam(lecturer, exam.getExamId(), newId, newTitle);
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter both ID and Title.");
                }
            }
        });

        btnDelete.addActionListener(e -> {
            int index = examList.getSelectedIndex();
            if (index == -1) {
                JOptionPane.showMessageDialog(this, "Select an exam to delete.");
                return;
            }
            Exam exam = exams.get(index);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete exam: " + exam.getExamId() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                examListModel.remove(index);
                exams.remove(index);
                lecturerService.deleteExam(lecturer, exam.getExamId());
            }
        });

        btnView.addActionListener(e -> {
            if (exams.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No exams available.");
            } else {
                StringBuilder sb = new StringBuilder();
                for (Exam exam : exams) {
                    sb.append(exam.getExamId()).append(" - ").append(exam.getTitle()).append("\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString(), "Exams List", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnAddQ.addActionListener(e -> {
            int index = examList.getSelectedIndex();
            if (index == -1) {
                JOptionPane.showMessageDialog(this, "Select an exam first.");
                return;
            }
            Exam exam = exams.get(index);
            JTextField qTextField = new JTextField();
            Object[] message = {"Question Text:", qTextField};
            int option = JOptionPane.showConfirmDialog(this, message, "Add Question", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String qText = qTextField.getText().trim();
                if (!qText.isEmpty()) {
                    Question q = new Question(0, "", qText, "", "SA", null, 1);
                    lecturerService.addQuestion(lecturer, exam, q);
                    JOptionPane.showMessageDialog(this, "Question added!");
                } else {
                    JOptionPane.showMessageDialog(this, "Enter question text.");
                }
            }
        });
    }
}
