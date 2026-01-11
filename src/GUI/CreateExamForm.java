// ...existing code...
package GUI;

import java.awt.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import models.Question;
import services.FileManager;

public class CreateExamForm {
    private JFrame frame;
    private final Path subjectsFile = Paths.get("data/subjects.txt");
    private final Path examsFile = Paths.get("data/exams.txt");

    public CreateExamForm() {
        frame = new JFrame("Create Exam");
        frame.setSize(480, 260);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        String bgPath = "D:\\Uni_Bedo\\Pl2\\Project_pl2\\PL_2-Project\\resources\\WhatsApp Image 2026-01-11 at 4.44.40 PM.jpeg";
        Image bg = UITheme.loadBackgroundImageAbsolute(bgPath);
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        background.setLayout(new GridBagLayout());
        frame.setContentPane(background);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8,10,8,10);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Create New Exam", JLabel.CENTER);
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.TEXT_COLOR);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        background.add(lblTitle, g);

        JLabel lblId = new JLabel("Exam ID:"); lblId.setFont(UITheme.FONT_LABEL); lblId.setForeground(UITheme.TEXT_COLOR);
        JTextField txtId = new JTextField(); UITheme.styleTextField(txtId);

        JLabel lblSubject = new JLabel("Subject:"); lblSubject.setFont(UITheme.FONT_LABEL); lblSubject.setForeground(UITheme.TEXT_COLOR);
        JComboBox<String> cbSubjects = new JComboBox<>(); cbSubjects.setFont(UITheme.FONT_BODY);

        JLabel lblDate = new JLabel("Date (YYYY-MM-DD):"); lblDate.setFont(UITheme.FONT_LABEL); lblDate.setForeground(UITheme.TEXT_COLOR);
        JTextField txtDate = new JTextField(); UITheme.styleTextField(txtDate);

        JLabel lblDuration = new JLabel("Duration (mins):"); lblDuration.setFont(UITheme.FONT_LABEL); lblDuration.setForeground(UITheme.TEXT_COLOR);
        JTextField txtDuration = new JTextField(); UITheme.styleTextField(txtDuration);

        // populate subjects from subjects.txt
        loadSubjects(cbSubjects);

        RoundedButton btnCreate = new RoundedButton("✓ Create Exam", UITheme.createLetterIcon("C", UITheme.PRIMARY_COLOR, Color.WHITE, 20));
        btnCreate.setBackground(UITheme.PRIMARY_COLOR);
        btnCreate.setFont(new Font("Arial", Font.BOLD, 13));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setPreferredSize(new Dimension(140, 40));
        
        RoundedButton btnCancel = new RoundedButton("✕ Cancel", UITheme.createLetterIcon("X", Color.RED, Color.WHITE, 18));
        btnCancel.setBackground(Color.RED.darker());
        btnCancel.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setPreferredSize(new Dimension(120, 40));

        g.gridx = 0; g.gridy = 1; g.gridwidth = 1; background.add(lblId, g);
        g.gridx = 1; g.gridy = 1; g.weightx = 1.0; background.add(txtId, g);

        g.gridx = 0; g.gridy = 2; g.weightx = 0; background.add(lblSubject, g);
        g.gridx = 1; g.gridy = 2; g.weightx = 1.0; background.add(cbSubjects, g);

        g.gridx = 0; g.gridy = 3; g.weightx = 0; background.add(lblDate, g);
        g.gridx = 1; g.gridy = 3; g.weightx = 1.0; background.add(txtDate, g);

        g.gridx = 0; g.gridy = 4; g.weightx = 0; background.add(lblDuration, g);
        g.gridx = 1; g.gridy = 4; g.weightx = 1.0; background.add(txtDuration, g);

        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT)); pBtns.setOpaque(false);
        pBtns.add(btnCreate); pBtns.add(btnCancel);
        g.gridx = 0; g.gridy = 5; g.gridwidth = 2; background.add(pBtns, g);

        btnCreate.addActionListener(e -> {
            String examId = txtId.getText().trim();
            if (examId.isEmpty()) { JOptionPane.showMessageDialog(frame, "Exam ID required"); return; }
            if (cbSubjects.getItemCount() == 0) { JOptionPane.showMessageDialog(frame, "No subjects available"); return; }
            String subjectItem = (String) cbSubjects.getSelectedItem();
            String subjectId = subjectItem != null ? subjectItem.split(" - ")[0].trim() : "";

            String line = examId + "," + subjectId;
            try {
                if (!Files.exists(examsFile)) {
                    Files.createDirectories(examsFile.getParent());
                    Files.write(examsFile, Arrays.asList("examId,subject"), StandardOpenOption.CREATE);
                }
                Files.write(examsFile, Arrays.asList(line), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                JOptionPane.showMessageDialog(frame, "Exam created successfully! Now add questions.");
                
                // Open dialog to add questions
                addQuestionsToExam(examId);
                
                frame.dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Failed to write exams file: " + ex.getMessage());
            }
        });

        btnCancel.addActionListener(e -> frame.dispose());

        frame.setVisible(true);
    }

    private void loadSubjects(JComboBox<String> cb) {
        cb.removeAllItems();
        if (!Files.exists(subjectsFile)) return;
        try {
            List<String> lines = Files.readAllLines(subjectsFile);
            boolean headerSkipped = false;
            for (String line : lines) {
                if (line == null) continue;
                line = line.trim();
                if (line.isEmpty()) continue;
                if (!headerSkipped) { headerSkipped = true; continue; } // skip header
                String[] parts = line.split(",", 2);
                String id = parts.length > 0 ? parts[0].trim() : "";
                String name = parts.length > 1 ? parts[1].trim() : "";
                cb.addItem(id + " - " + name);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Failed to read subjects file: " + ex.getMessage());
        }
    }

    private void addQuestionsToExam(String examId) {
        // Ask for number of questions
        String numStr = JOptionPane.showInputDialog(frame, "How many questions to add?");
        if (numStr == null || numStr.trim().isEmpty()) return;
        int numQuestions;
        try {
            numQuestions = Integer.parseInt(numStr.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid number.");
            return;
        }
        
        java.util.List<Question> questions = new java.util.ArrayList<>();
        int questionId = 1; // Start from 1 for this exam
        
        for (int i = 0; i < numQuestions; i++) {
            // Dialog for each question
            JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
            JTextArea questionText = new JTextArea(3, 20);
            questionText.setLineWrap(true);
            JScrollPane scroll = new JScrollPane(questionText);
            JTextField correctAnswer = new JTextField();
            
            panel.add(new JLabel("Question " + (i+1) + " Text:"));
            panel.add(scroll);
            panel.add(new JLabel("Correct Answer:"));
            panel.add(correctAnswer);
            
            int result = JOptionPane.showConfirmDialog(frame, panel, "Add Question", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) continue;
            
            String qText = questionText.getText().trim();
            String ans = correctAnswer.getText().trim();
            if (qText.isEmpty() || ans.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Question and answer required.");
                i--; // Retry
                continue;
            }
            
            Question q = new Question(questionId++, qText, ans);
            q.setExamId(examId);
            questions.add(q);
        }
        
        if (!questions.isEmpty()) {
            // Save questions using FileManager
            FileManager.saveQuestions(questions);
            JOptionPane.showMessageDialog(frame, "Questions added successfully!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            new CreateExamForm();
        });
    }
}
// ...existing code...