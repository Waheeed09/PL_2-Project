package GUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.*;
import java.io.IOException;
import models.Question;
import services.StudentService;

public class TakeExamForm extends JFrame {

    private int studentId;
    private StudentService studentService = new StudentService();
    private final Path examsFile = Paths.get("d:\\VSC\\CollegeExaminationSystemPage\\PL_2-Project\\data\\exams.txt");

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
            List<String[]> exams = loadExamsFromFile();
            if (exams.isEmpty()) {
                area.setText("No exams available!");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (String[] ex : exams) {
                sb.append(ex[0]).append(" - ").append(ex[1]).append("\n");
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

    private List<String[]> loadExamsFromFile() {
        List<String[]> list = new ArrayList<>();
        if (!Files.exists(examsFile)) return list;
        try {
            List<String> lines = Files.readAllLines(examsFile);
            boolean headerSkipped = false;
            for (String line : lines) {
                if (line == null) continue;
                line = line.trim();
                if (line.isEmpty()) continue;
                if (!headerSkipped) { headerSkipped = true; continue; } // skip header
                String[] parts = line.split(",", 2);
                String id = parts.length > 0 ? parts[0].trim() : "";
                String title = parts.length > 1 ? parts[1].trim() : "";
                list.add(new String[]{id, title});
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to read exams file: " + ex.getMessage());
        }
        return list;
    }

    private void startExam(String examId) {
        ArrayList<Question> questions = studentService.loadQuestions(examId);
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
