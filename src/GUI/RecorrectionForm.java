package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import models.*;

public class RecorrectionForm extends JFrame {
    private final Student student;

    public RecorrectionForm(Student student) {
        this.student = student;

        setTitle("Request Recorrection - " + student.getName());
        setSize(560, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        background.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JLabel titleLabel = new JLabel("Request Recorrection", JLabel.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_COLOR);
        background.add(titleLabel, BorderLayout.NORTH);

        JTextArea messageArea = new JTextArea();
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(UITheme.FONT_BODY);
        messageArea.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR), BorderFactory.createEmptyBorder(8,8,8,8)));
        JScrollPane scrollPane = new JScrollPane(messageArea);
        background.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        RoundedButton submitButton = new RoundedButton("Submit"); submitButton.setBackground(UITheme.PRIMARY_COLOR);
        RoundedButton cancelButton = new RoundedButton("Cancel"); cancelButton.setBackground(UITheme.BORDER_COLOR);
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        background.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(background);

        // Event handlers
        submitButton.addActionListener(e -> {
            String message = messageArea.getText().trim();
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a message.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (saveRecorrection(message)) {
                JOptionPane.showMessageDialog(this, "Recorrection request submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error saving recorrection request.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> this.dispose());

        setVisible(true);
    }

    private boolean saveRecorrection(String message) {
        try {
            new File("data").mkdirs();
            String recorrectionData = student.getId() + "|" + student.getName() + "|" + message + "|" + System.currentTimeMillis();
            try (java.io.FileWriter fw = new java.io.FileWriter("data/recorrections.txt", true)) {
                fw.write(recorrectionData + "\n");
            }
            return true;
        } catch (IOException ex) {
            System.err.println("Error saving recorrection: " + ex.getMessage());
            return false;
        }
    }
}
