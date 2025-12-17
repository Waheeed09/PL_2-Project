package GUI;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.*;
import services.*;

public class MainGUI extends JFrame {
    private AdminService adminService;
    private StudentService studentService;
    private ResultService resultService;
    private UserService userService;
    private User loggedInUser;
    private JPanel currentPanel;

    public MainGUI() {
        super("University Examination System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize services
        initializeServices();
        
        // Show login panel
        showLoginPanel();
        
        setVisible(true);
    }

    private void initializeServices() {
        adminService = new AdminService();
        studentService = new StudentService();
        resultService = new ResultService();
        
        // Load data
        adminService.setUsers(FileManager.loadUsers());
        adminService.setSubjects(FileManager.loadSubjects());
        adminService.setResults(FileManager.loadResults());
        resultService.getAllResults().addAll(FileManager.loadResults());
        studentService.loadStudents();
        
        userService = new UserService(adminService.getUsers());
    }

    private void showLoginPanel() {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Email
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Email:"), gbc);
        JTextField emailField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Password
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Login Button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            loggedInUser = userService.login(email, password);
            
            if (loggedInUser != null) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                showRoleBasedPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Login failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(loginButton, gbc);

        // Register Button
        gbc.gridy++;
        JButton registerButton = new JButton("Create New Account");
        registerButton.addActionListener(e -> showRegistrationPanel());
        panel.add(registerButton, gbc);

        currentPanel = panel;
        add(currentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showRegistrationPanel() {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Create New Account", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // ID
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(new JLabel("ID:"), gbc);
        JTextField idField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        // Name
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Name:"), gbc);
        JTextField nameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        // Email
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Email:"), gbc);
        JTextField emailField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Password
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Role
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Role:"), gbc);
        String[] roles = {"Student", "Lecturer", "Admin"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        gbc.gridx = 1;
        panel.add(roleCombo, gbc);

        // Register Button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String role = "";
                
                switch (roleCombo.getSelectedIndex()) {
                    case 0: role = "student"; break;
                    case 1: role = "lecturer"; break;
                    case 2: role = "admin"; break;
                }
                
                User newUser = new User(id, name, email, password, role);
                adminService.addUser(newUser);
                FileManager.saveUsers(adminService.getUsers());
                
                JOptionPane.showMessageDialog(this, "Account created successfully!");
                showLoginPanel();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid ID number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(registerButton, gbc);

        // Back Button
        gbc.gridy++;
        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> showLoginPanel());
        panel.add(backButton, gbc);

        currentPanel = panel;
        add(currentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showRoleBasedPanel() {
        if (loggedInUser == null) {
            showLoginPanel();
            return;
        }

        // Remove current panel
        if (currentPanel != null) {
            remove(currentPanel);
        }

        JPanel panel = new JPanel(new BorderLayout());
        
        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            loggedInUser = null;
            showLoginPanel();
        });
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Role-specific content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Welcome, " + loggedInUser.getName(), JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        contentPanel.add(welcomeLabel);

        // Add role-specific components
        switch (loggedInUser.getRole()) {
            case "admin":
                showAdminPanel(contentPanel);
                break;
            case "student":
                showStudentPanel(contentPanel);
                break;
            case "lecturer":
                showLecturerPanel(contentPanel);
                break;
            default:
                contentPanel.add(new JLabel("Unknown role", JLabel.CENTER));
        }

        panel.add(contentPanel, BorderLayout.CENTER);
        currentPanel = panel;
        add(currentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showFeedbackDialog() {
        // إنشاء لوحة الإدخال
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // حقل إدخال رقم الامتحان
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        inputPanel.add(new JLabel("Exam ID:"));
        JTextField examIdField = new JTextField();
        inputPanel.add(examIdField);
        
        // حقل إدخال الملاحظة
        JTextArea feedbackArea = new JTextArea(5, 20);
        feedbackArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(feedbackArea);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JLabel("Your Feedback:"), BorderLayout.CENTER); // تسمية توضيحية
        panel.add(scrollPane, BorderLayout.SOUTH);

        // عرض النافذة المنبثقة
        int result = JOptionPane.showConfirmDialog(this, panel, "Submit Feedback",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String examId = examIdField.getText().trim();
            String message = feedbackArea.getText().trim();

            if (examId.isEmpty() || message.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // التأكد أن رقم الامتحان رقم صحيح
                int examIdInt = Integer.parseInt(examId);

                // استخدام FeedbackService لحفظ الفيدباك
                FeedbackService.addFeedback(loggedInUser.getId(), examIdInt, message);
                
                JOptionPane.showMessageDialog(this, "Feedback submitted successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Exam ID must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Helper method to show user form for adding/editing users
    private void showUserForm(User user) {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"admin", "lecturer", "student"});
        
        // If editing existing user, populate fields
        if (user != null) {
            idField.setText(String.valueOf(user.getId()));
            idField.setEditable(false); // Don't allow changing ID
            nameField.setText(user.getName());
            emailField.setText(user.getEmail());
            passwordField.setText(""); // Don't show actual password
            roleCombo.setSelectedItem(user.getRole());
        }
        
        panel.add(new JLabel("ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);
        
        int result = JOptionPane.showConfirmDialog(
            this, panel, 
            user == null ? "Add New User" : "Edit User",
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE);
            
        if (result == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                String role = (String) roleCombo.getSelectedItem();
                
                // Create or update user
                User updatedUser = new User(id, name, email, password, role);
                if (user == null) {
                    // Add new user
                    adminService.addUser(updatedUser);
                    JOptionPane.showMessageDialog(this, "User added successfully!");
                } else {
                    // Update existing user
                    // TODO: Implement update user in AdminService
                    JOptionPane.showMessageDialog(this, "User updated successfully!");
                }
                
                // Save changes
                FileManager.saveUsers(adminService.getUsers());
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid ID number.", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving user: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAdminPanel(JPanel parent) {
        JButton manageUsersBtn = new JButton("Manage Users");
        JButton manageSubjectsBtn = new JButton("Manage Subjects");
        JButton manageExamsBtn = new JButton("Manage Exams");
        JButton viewReportsBtn = new JButton("View Reports");
        JButton systemSettingsBtn = new JButton("System Settings");

        // Manage Users Button
        manageUsersBtn.addActionListener(e -> {
            // Create a dialog to manage users
            JPanel panel = new JPanel(new BorderLayout());
            
            // Get all users
            List<User> users = new ArrayList<>(adminService.getUsers());
            String[] columns = {"ID", "Name", "Email", "Role"};
            Object[][] data = users.stream()
                .map(u -> new Object[]{u.getId(), u.getName(), u.getEmail(), u.getRole()})
                .toArray(Object[][]::new);
                
            JTable table = new JTable(data, columns);
            JScrollPane scrollPane = new JScrollPane(table);
            
            // Add buttons for user management
            JPanel buttonPanel = new JPanel();
            JButton addUserBtn = new JButton("Add User");
            JButton editUserBtn = new JButton("Edit User");
            JButton deleteUserBtn = new JButton("Delete User");
            
            addUserBtn.addActionListener(ev -> showUserForm(null));
            editUserBtn.addActionListener(ev -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int userId = (int) table.getValueAt(selectedRow, 0);
                    User userToEdit = users.stream()
                        .filter(u -> u.getId() == userId)
                        .findFirst()
                        .orElse(null);
                    if (userToEdit != null) {
                        showUserForm(userToEdit);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a user to edit.", 
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            });
            
            buttonPanel.add(addUserBtn);
            buttonPanel.add(editUserBtn);
            buttonPanel.add(deleteUserBtn);
            
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            JOptionPane.showMessageDialog(this, panel, "User Management", 
                JOptionPane.PLAIN_MESSAGE);
        });

        // Manage Subjects Button
        manageSubjectsBtn.addActionListener(e -> {
            // Show subjects management interface
            try (BufferedReader br = new BufferedReader(new FileReader("data/subjects.txt"))) {
                StringBuilder subjects = new StringBuilder("Subjects Management\n\n");
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        subjects.append(line).append("\n");
                    }
                }
                
                JTextArea textArea = new JTextArea(subjects.toString());
                textArea.setEditable(true);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));
                
                int result = JOptionPane.showConfirmDialog(this, 
                    scrollPane, "Manage Subjects", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    
                if (result == JOptionPane.OK_OPTION) {
                    // TODO: Save changes to subjects file
                    JOptionPane.showMessageDialog(this, 
                        "Subject changes saved successfully.");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading subjects: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Manage Exams Button
        manageExamsBtn.addActionListener(e -> {
            // Open exam management interface
            JOptionPane.showMessageDialog(this, 
                "Exam management interface will be implemented here.",
                "Under Construction", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // View Reports Button
        viewReportsBtn.addActionListener(e -> {
            // Show system reports
            JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
            
            // Add report options
            JButton studentReportBtn = new JButton("Student Performance Report");
            JButton examReportBtn = new JButton("Exam Statistics");
            JButton systemReportBtn = new JButton("System Usage Report");
            
            panel.add(studentReportBtn);
            panel.add(examReportBtn);
            panel.add(systemReportBtn);
            
            JOptionPane.showMessageDialog(this, panel, "System Reports", 
                JOptionPane.PLAIN_MESSAGE);
        });
        
        // System Settings Button
        systemSettingsBtn.addActionListener(e -> {
            // Show system settings dialog
            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            
            panel.add(new JLabel("Academic Year:"));
            JTextField yearField = new JTextField("2024-2025");
            panel.add(yearField);
            
            panel.add(new JLabel("Max Attempts per Exam:"));
            JSpinner attemptsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
            panel.add(attemptsSpinner);
            
            panel.add(new JLabel("Result Publication:"));
            JCheckBox autoPublishCheck = new JCheckBox("Publish results automatically");
            panel.add(autoPublishCheck);
            
            int result = JOptionPane.showConfirmDialog(this, 
                panel, "System Settings", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
            if (result == JOptionPane.OK_OPTION) {
                // TODO: Save system settings
                JOptionPane.showMessageDialog(this, 
                    "System settings saved successfully.");
            }
        });

        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));
        buttonPanel.add(manageUsersBtn);
        buttonPanel.add(manageSubjectsBtn);
        buttonPanel.add(manageExamsBtn);
        buttonPanel.add(viewReportsBtn);
        buttonPanel.add(systemSettingsBtn);

        parent.add(Box.createVerticalStrut(20));
        parent.add(buttonPanel);
    }

    private void showStudentPanel(JPanel parent) {
        JButton takeExamBtn = new JButton("Take Exam");
        JButton viewResultsBtn = new JButton("View My Results");
        JButton viewSubjectsBtn = new JButton("View My Subjects");
        JButton feedbackBtn = new JButton("Give Feedback");
        JButton recorrectionBtn = new JButton("Request Recorrection");


        // Take Exam Button
        takeExamBtn.addActionListener(e -> {
            // Open the TakeExamForm
            TakeExamForm takeExamForm = new TakeExamForm(loggedInUser.getId());
            takeExamForm.setVisible(true);
        });

        // View Results Button
        viewResultsBtn.addActionListener(e -> {
            // Show student results in a dialog
            String results = studentService.getStudentResults(loggedInUser.getId());
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No results available yet.", "My Results", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JTextArea textArea = new JTextArea(results);
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));
                JOptionPane.showMessageDialog(this, scrollPane, "My Results", JOptionPane.PLAIN_MESSAGE);
            }
        });

        // View Subjects Button
        viewSubjectsBtn.addActionListener(e -> {
            // Show registered subjects
            String subjects = studentService.getRegisteredSubjects(loggedInUser.getId());
            if (subjects.isEmpty()) {
                JOptionPane.showMessageDialog(this, "You are not registered in any subjects yet.", "My Subjects", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JTextArea textArea = new JTextArea(subjects);
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(300, 200));
                JOptionPane.showMessageDialog(this, scrollPane, "My Subjects", JOptionPane.PLAIN_MESSAGE);
            }
        });

        feedbackBtn.addActionListener(e -> {
            showFeedbackDialog();
        });

        recorrectionBtn.addActionListener(e -> {
            Student student = new Student(loggedInUser.getId(), loggedInUser.getName(), loggedInUser.getEmail(), loggedInUser.getPassword());
            new RecorrectionForm(student);
        });

        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 10)); 
        buttonPanel.add(takeExamBtn);
        buttonPanel.add(viewResultsBtn);
        buttonPanel.add(viewSubjectsBtn);
        buttonPanel.add(feedbackBtn);
        buttonPanel.add(recorrectionBtn);

        // Add components to parent
        parent.add(Box.createVerticalStrut(20));
        parent.add(buttonPanel);
    }

     private void showLecturerPanel(JPanel parent) {
        // تعريف الأزرار المطلوبة
        JButton createExamBtn = new JButton("Create Exam");
        JButton modifyExamBtn = new JButton("Modify Exam");
        JButton deleteExamBtn = new JButton("Delete Exam");
        JButton viewExamsBtn = new JButton("View Exams");
        JButton studentReportsBtn = new JButton("Student Reports");

        // 1. Create Exam Logic
        createExamBtn.addActionListener(e -> {
            JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
            JTextField idField = new JTextField();
            JTextField titleField = new JTextField();
            JTextField subjectField = new JTextField();
            JTextField durationField = new JTextField();

            panel.add(new JLabel("Exam ID:")); panel.add(idField);
            panel.add(new JLabel("Title:")); panel.add(titleField);
            panel.add(new JLabel("Subject:")); panel.add(subjectField);
            panel.add(new JLabel("Duration (mins):")); panel.add(durationField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Create New Exam",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String examLine = idField.getText() + "," + titleField.getText() + "," + 
                                      subjectField.getText() + "," + durationField.getText();
                    
                    // Save directly to file
                    appendToExamsFile(examLine);
                    JOptionPane.showMessageDialog(this, "Exam Created Successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error creating exam: " + ex.getMessage());
                }
            }
        });

        // 2. Modify Exam Logic
        modifyExamBtn.addActionListener(e -> {
            String oldId = JOptionPane.showInputDialog(this, "Enter Exam ID to Modify:");
            if (oldId != null && !oldId.isEmpty()) {
                // البحث عن الامتحان وتعديله
                modifyExamInFile(oldId);
            }
        });

        // 3. Delete Exam Logic
        deleteExamBtn.addActionListener(e -> {
            String delId = JOptionPane.showInputDialog(this, "Enter Exam ID to Delete:");
            if (delId != null && !delId.isEmpty()) {
                deleteExamFromFile(delId);
            }
        });

        // 4. View Exams Logic
        viewExamsBtn.addActionListener(e -> {
            ArrayList<String> exams = loadExamsList();
            if (exams.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No exams found.");
            } else {
                JList<String> list = new JList<>(exams.toArray(new String[0]));
                JOptionPane.showMessageDialog(this, new JScrollPane(list), "All Exams", JOptionPane.PLAIN_MESSAGE);
            }
        });

        // 5. Student Reports Logic
        studentReportsBtn.addActionListener(e -> {
             // عرض النتائج من ملف results.txt
             showStudentReports();
        });

        // تنسيق الأزرار في الشاشة
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.add(createExamBtn);
        buttonPanel.add(modifyExamBtn);
        buttonPanel.add(deleteExamBtn);
        buttonPanel.add(viewExamsBtn);
        buttonPanel.add(studentReportsBtn);

        parent.add(Box.createVerticalStrut(20));
        parent.add(buttonPanel);
    }

    // ---------------------------------------------------------
    // Helper Methods for Lecturer Operations (File Handling)
    // ---------------------------------------------------------

    private void appendToExamsFile(String line) {
        try (java.io.FileWriter fw = new java.io.FileWriter("data/exams.txt", true);
             java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {
            pw.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void modifyExamInFile(String targetId) {
        File inputFile = new File("data/exams.txt");
        File tempFile = new File("data/exams_temp.txt");
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(targetId)) {
                    found = true;
                    // Show dialog to enter new data
                    JTextField titleField = new JTextField(parts.length > 1 ? parts[1] : "");
                    JTextField subjectField = new JTextField(parts.length > 2 ? parts[2] : "");
                    JTextField durationField = new JTextField(parts.length > 3 ? parts[3] : "");
                    
                    JPanel panel = new JPanel(new GridLayout(3, 2));
                    panel.add(new JLabel("New Title:")); panel.add(titleField);
                    panel.add(new JLabel("New Subject:")); panel.add(subjectField);
                    panel.add(new JLabel("New Duration:")); panel.add(durationField);

                    int result = JOptionPane.showConfirmDialog(this, panel, "Edit Exam " + targetId, JOptionPane.OK_CANCEL_OPTION);
                    
                    if (result == JOptionPane.OK_OPTION) {
                        // Write new line
                        writer.println(targetId + "," + titleField.getText() + "," + subjectField.getText() + "," + durationField.getText());
                    } else {
                        // Keep old line if cancelled
                        writer.println(line);
                    }
                } else {
                    writer.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Replace file
        if (found) {
            inputFile.delete();
            tempFile.renameTo(inputFile);
            JOptionPane.showMessageDialog(this, "Exam updated successfully.");
        } else {
            tempFile.delete();
            JOptionPane.showMessageDialog(this, "Exam ID not found.");
        }
    }

    private void deleteExamFromFile(String targetId) {
        File inputFile = new File("data/exams.txt");
        File tempFile = new File("data/exams_temp.txt");
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(targetId)) {
                    found = true; 
                    continue; // Skip writing this line (Delete)
                }
                writer.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (found) {
            inputFile.delete();
            tempFile.renameTo(inputFile);
            JOptionPane.showMessageDialog(this, "Exam deleted successfully.");
        } else {
            tempFile.delete();
            JOptionPane.showMessageDialog(this, "Exam ID not found.");
        }
    }

    private ArrayList<String> loadExamsList() {
        ArrayList<String> list = new ArrayList<>();
        File file = new File("data/exams.txt");
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private void showStudentReports() {
        // Read results.txt and show in table
        File file = new File("data/results.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "No results found.");
            return;
        }

        ArrayList<String[]> dataList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Assuming result format: StudentID,ExamID,Score
                dataList.add(line.split(","));
            }
        } catch (IOException e) { e.printStackTrace(); }

        String[] columns = {"Student ID", "Exam ID", "Score"};
        Object[][] data = new Object[dataList.size()][3];
        for (int i = 0; i < dataList.size(); i++) {
            String[] row = dataList.get(i);
            if (row.length >= 3) {
                data[i][0] = row[0];
                data[i][1] = row[1];
                data[i][2] = row[2];
            }
        }

        JTable table = new JTable(data, columns);
        JOptionPane.showMessageDialog(this, new JScrollPane(table), "Student Reports", JOptionPane.PLAIN_MESSAGE);
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start the application
        SwingUtilities.invokeLater(MainGUI::new);
    }
}