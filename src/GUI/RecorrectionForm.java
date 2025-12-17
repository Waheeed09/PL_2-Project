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
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel titleLabel = new JLabel("Request Recorrection", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Message area
        JPanel textPanel = new JPanel(new BorderLayout(5, 5));
        JLabel messageLabel = new JLabel("Describe your recorrection request:");
        JTextArea messageArea = new JTextArea(15, 40);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(messageArea);
        textPanel.add(messageLabel, BorderLayout.NORTH);
        textPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(textPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

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
            // Ensure data directory exists
            new File("data").mkdirs();

            // Create recorrection entry
            String recorrectionData = student.getId() + "|" + student.getName() + "|" + message + "|" + System.currentTimeMillis();

            // Append to recorrections.txt
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
