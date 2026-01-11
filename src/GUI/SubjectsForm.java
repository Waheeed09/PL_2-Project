package GUI;

import java.awt.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SubjectsForm {
    private final Path subjectsFile = Paths.get("data/subjects.txt");
    private JFrame frame;
    private DefaultTableModel model;

    public SubjectsForm() {
        frame = new JFrame("Subjects");
        frame.setSize(720, 480);
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
        background.setLayout(new BorderLayout());
        background.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        frame.setContentPane(background);

        model = new DefaultTableModel(new String[]{"ID", "Name", "LecturerId", "Enrolled"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        JScrollPane sp = new JScrollPane(table);

        JPanel top = new JPanel(new BorderLayout()); top.setOpaque(false);
        JLabel title = new JLabel("Subjects", JLabel.LEFT); title.setFont(UITheme.FONT_TITLE); title.setForeground(UITheme.TEXT_COLOR);
        top.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); btnPanel.setOpaque(false);
        RoundedButton btnAdd = new RoundedButton("Add"); btnAdd.setBackground(UITheme.PRIMARY_COLOR);
        RoundedButton btnAssign = new RoundedButton("Assign Lecturer"); btnAssign.setBackground(UITheme.PRIMARY_COLOR.darker());
        RoundedButton btnEnroll = new RoundedButton("Enroll Student"); btnEnroll.setBackground(UITheme.PRIMARY_COLOR.darker());
        RoundedButton btnRefresh = new RoundedButton("Refresh"); btnRefresh.setBackground(UITheme.BORDER_COLOR);
        btnPanel.add(btnAdd); btnPanel.add(btnAssign); btnPanel.add(btnEnroll); btnPanel.add(btnRefresh);
        top.add(btnPanel, BorderLayout.EAST);

        background.add(top, BorderLayout.NORTH);
        background.add(sp, BorderLayout.CENTER);

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
            if (!Files.exists(subjectsFile)) return;
            List<String> lines = Files.readAllLines(subjectsFile);
            boolean headerSkipped = false;
            for (String line : lines) {
                if (line == null) continue;
                line = line.trim();
                if (line.isEmpty()) continue;
                if (!headerSkipped) { headerSkipped = true; continue; }
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
