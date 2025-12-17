// ...existing code...
package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.nio.file.*;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;

public class SubjectsForm {
    private final Path subjectsFile = Paths.get("d:\\VSC\\CollegeExaminationSystemPage\\PL_2-Project\\data\\subjects.txt");
    private JFrame frame;
    private DefaultTableModel model;

    public SubjectsForm() {
        frame = new JFrame("Subjects");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);

        model = new DefaultTableModel(new String[]{"ID", "Name", "LecturerId", "Enrolled"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(10, 10, 560, 280);

        JButton btnAdd = new JButton("Add");
        JButton btnAssign = new JButton("Assign Lecturer");
        JButton btnEnroll = new JButton("Enroll Student");
        JButton btnRefresh = new JButton("Refresh");

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd);
        btnPanel.add(btnAssign);
        btnPanel.add(btnEnroll);
        btnPanel.add(btnRefresh);
        btnPanel.setBounds(10, 300, 560, 40);

        frame.add(sp);
        frame.add(btnPanel);

        btnAdd.addActionListener(e -> addSubject());
        btnAssign.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Assign Lecturer not supported with subjects.txt backend"));
        btnEnroll.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Enroll Student not supported with subjects.txt backend"));
        btnRefresh.addActionListener(e -> loadData());

        loadData();
        frame.setVisible(true);
    }

    private void loadData() {
        model.setRowCount(0);
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
                model.addRow(new Object[]{ id, name, "-", 0 });
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to read subjects file: " + e.getMessage());
        }
    }

    private void addSubject() {
        String id = JOptionPane.showInputDialog(frame, "Subject ID:");
        if (id == null || id.trim().isEmpty()) return;
        String name = JOptionPane.showInputDialog(frame, "Subject Name:");
        if (name == null || name.trim().isEmpty()) return;

        String line = id.trim() + "," + name.trim();
        try {
            // ensure file exists with header
            if (!Files.exists(subjectsFile)) {
                Files.createDirectories(subjectsFile.getParent());
                Files.write(subjectsFile, Arrays.asList("subjectId,subjectName"), StandardOpenOption.CREATE);
            }
            Files.write(subjectsFile, Arrays.asList(line), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            JOptionPane.showMessageDialog(frame, "Added");
            loadData();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Failed to add subject: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            new SubjectsForm();
        });
    }
}
// ...existing code...