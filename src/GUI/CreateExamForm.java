package GUI;

import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;

public class CreateExamForm {
    private JFrame frame;
    private final Path subjectsFile = Paths.get("d:\\VSC\\CollegeExaminationSystemPage\\PL_2-Project\\data\\subjects.txt");
    private final Path examsFile = Paths.get("d:\\VSC\\CollegeExaminationSystemPage\\PL_2-Project\\data\\exams.txt");

    public CreateExamForm() {
        frame = new JFrame("Create Exam");
        frame.setSize(420, 220);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblId = new JLabel("Exam ID:");
        JTextField txtId = new JTextField();

        JLabel lblSubject = new JLabel("Subject:");
        JComboBox<String> cbSubjects = new JComboBox<>();

        JLabel lblDate = new JLabel("Date (YYYY-MM-DD):");
        JTextField txtDate = new JTextField();

        JLabel lblDuration = new JLabel("Duration (mins):");
        JTextField txtDuration = new JTextField();

        // populate subjects from subjects.txt
        loadSubjects(cbSubjects);

        JButton btnCreate = new JButton("Create");
        JButton btnCancel = new JButton("Cancel");

        g.gridx = 0; g.gridy = 0; frame.add(lblId, g);
        g.gridx = 1; g.gridy = 0; g.weightx = 1.0; frame.add(txtId, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0; frame.add(lblSubject, g);
        g.gridx = 1; g.gridy = 1; g.weightx = 1.0; frame.add(cbSubjects, g);

        g.gridx = 0; g.gridy = 2; g.weightx = 0; frame.add(lblDate, g);
        g.gridx = 1; g.gridy = 2; g.weightx = 1.0; frame.add(txtDate, g);

        g.gridx = 0; g.gridy = 3; g.weightx = 0; frame.add(lblDuration, g);
        g.gridx = 1; g.gridy = 3; g.weightx = 1.0; frame.add(txtDuration, g);

        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pBtns.add(btnCreate); pBtns.add(btnCancel);
        g.gridx = 0; g.gridy = 4; g.gridwidth = 2; frame.add(pBtns, g);

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
                JOptionPane.showMessageDialog(frame, "Exam saved to exams.txt");
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            new CreateExamForm();
        });
    }
}