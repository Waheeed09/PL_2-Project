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
        setSize(520, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String bgPath = "D:\\Uni_Bedo\\Pl2\\Project_pl2\\PL_2-Project\\resources\\WhatsApp Image 2026-01-11 at 4.44.40 PM.jpeg";
        Image bg = UITheme.loadBackgroundImageAbsolute(bgPath);
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        background.setLayout(new BorderLayout(10,10));
        background.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        setContentPane(background);

        JLabel title = new JLabel("Available Exams", JLabel.CENTER);
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_COLOR);
        background.add(title, BorderLayout.NORTH);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> examsList = new JList<>(listModel);
        examsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        examsList.setFont(UITheme.FONT_BODY);
        JScrollPane scrollPane = new JScrollPane(examsList);
        background.add(scrollPane, BorderLayout.CENTER);

        RoundedButton refreshBtn = new RoundedButton("Refresh Exams"); refreshBtn.setBackground(UITheme.PRIMARY_COLOR);
        RoundedButton startBtn = new RoundedButton("Start Exam"); startBtn.setBackground(UITheme.PRIMARY_COLOR.darker());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); topPanel.setOpaque(false);
        topPanel.add(refreshBtn); topPanel.add(startBtn);
        background.add(topPanel, BorderLayout.SOUTH);

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
