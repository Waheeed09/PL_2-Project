package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import models.Lecturer;
import models.Exam;
import models.Question;
import models.User;
import services.LecturerService;

public class LecturerDashboard extends JFrame {

    private Lecturer lecturer;
    private LecturerService lecturerService;

    private DefaultListModel<String> examListModel;
    private JList<String> examList;
    private List<Exam> exams; // لتخزين الكائنات الفعلية

    // ======= الكونستركتور المعدل =======
    public LecturerDashboard(User user) {
        if (!(user instanceof Lecturer)) {
            throw new IllegalArgumentException("User must be a Lecturer");
        }
        this.lecturer = (Lecturer) user;
        this.lecturerService = new LecturerService();
        this.exams = new ArrayList<>();

        setTitle("Lecturer Dashboard - " + lecturer.getName());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        examListModel = new DefaultListModel<>();
        examList = new JList<>(examListModel);
        JScrollPane scrollPane = new JScrollPane(examList);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 5, 5));

        JButton btnCreate = new JButton("Create Exam");
        JButton btnModify = new JButton("Modify Exam");
        JButton btnDelete = new JButton("Delete Exam");
        JButton btnView = new JButton("View Exams");
        JButton btnAddQ = new JButton("Add Question");

        buttonPanel.add(btnCreate);
        buttonPanel.add(btnModify);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnView);
        buttonPanel.add(btnAddQ);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Actions
        btnCreate.addActionListener(e -> createExam());
        btnModify.addActionListener(e -> modifyExam());
        btnDelete.addActionListener(e -> deleteExam());
        btnView.addActionListener(e -> viewExams());
        btnAddQ.addActionListener(e -> addQuestion());
    }

    // ======== العمليات ========
    private void createExam() {
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
    }

    private void modifyExam() {
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
    }

    private void deleteExam() {
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
    }

    private void viewExams() {
        if (exams.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No exams available.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Exam e : exams) {
                sb.append(e.getExamId()).append(" - ").append(e.getTitle()).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Exams List", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addQuestion() {
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
    }
}
