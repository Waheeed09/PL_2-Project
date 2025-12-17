package GUI;

import javax.swing.*;
import java.awt.*;
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
        setLayout(new BorderLayout(10, 10));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> examsList = new JList<>(listModel);
        examsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(examsList);

        JButton refreshBtn = new JButton("Refresh Exams");
        JButton startBtn = new JButton("Start Exam");
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.add(refreshBtn);
        topPanel.add(startBtn);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        Runnable refresh = () -> {
            listModel.clear();
            ArrayList<Exam> exams = studentService.loadExams();
            for (Exam ex : exams) {
                String examId = ex.getExamId();
                if (examId == null || examId.trim().isEmpty()) {
                    continue;
                }
                if (!studentService.hasTakenExam(studentId, examId)) {
                    listModel.addElement(examId + " - " + ex.getTitle());
                }
            }

            if (listModel.isEmpty()) {
                listModel.addElement("No available exams.");
                examsList.setEnabled(false);
                startBtn.setEnabled(false);
            } else {
                examsList.setEnabled(true);
                startBtn.setEnabled(true);
                examsList.setSelectedIndex(0);
            }
        };

        refreshBtn.addActionListener(e -> refresh.run());
        startBtn.addActionListener(e -> {
            String selected = examsList.getSelectedValue();
            if (selected == null || selected.equals("No available exams.")) {
                JOptionPane.showMessageDialog(this, "No exam selected.");
                return;
            }

            String examId = selected.split(" - ", 2)[0].trim();
            if (studentService.hasTakenExam(studentId, examId)) {
                JOptionPane.showMessageDialog(this, "You have already submitted this exam!");
                refresh.run();
                return;
            }

            startExam(examId);
            refresh.run();
        });

        refresh.run();

        setVisible(true);
    }

    private void startExam(String examId) {
        ArrayList<Question> questions = studentService.loadQuestions(examId);
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions found for this exam.");
            return;
        }
        for (Question q : questions) {
            String ans = JOptionPane.showInputDialog(this, "Question:\n" + q.getText());
            studentService.answerQuestion(studentId, q.getId(), ans);
        }
        studentService.saveSubmission(studentId, examId);
        JOptionPane.showMessageDialog(this, "Exam submitted!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TakeExamForm(1));
    }

}
