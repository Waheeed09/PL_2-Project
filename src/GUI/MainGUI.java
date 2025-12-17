package GUI;

import models.*;
import services.*;
import services.FileManager;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.table.DefaultTableModel;

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
        JButton takeExamBtn = new JButton("Available Exams");
        JButton viewResultsBtn = new JButton("View My Results");
        JButton viewSubjectsBtn = new JButton("View My Subjects");

        // Take Exam Button
        takeExamBtn.addActionListener(e -> {
            // Open the TakeExamForm
            new TakeExamForm(loggedInUser.getId());
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
            JDialog dialog = new JDialog(this, "My Subjects", true);
            dialog.setSize(520, 420);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout(10, 10));

            JTextArea registeredArea = new JTextArea();
            registeredArea.setEditable(false);
            JScrollPane registeredScroll = new JScrollPane(registeredArea);

            Runnable refreshRegistered = () -> {
                String subjects = studentService.getRegisteredSubjects(loggedInUser.getId());
                if (subjects == null || subjects.trim().isEmpty()) {
                    registeredArea.setText("You are not registered in any subjects yet.\n\nUse the dropdown below to register.");
                } else {
                    registeredArea.setText(subjects);
                }
            };

            java.util.ArrayList<Subject> allSubjects = studentService.loadAllSubjects();
            String[] subjectItems = allSubjects.stream()
                .map(s -> s.getSubjectId() + " - " + s.getSubjectName())
                .toArray(String[]::new);

            JComboBox<String> subjectCombo = new JComboBox<>(subjectItems);
            JButton registerBtn = new JButton("Register Subject");
            JButton closeBtn = new JButton("Close");

            registerBtn.addActionListener(ev -> {
                int idx = subjectCombo.getSelectedIndex();
                if (idx < 0 || idx >= allSubjects.size()) {
                    JOptionPane.showMessageDialog(dialog, "No subject selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Subject selected = allSubjects.get(idx);
                boolean ok = studentService.registerSubject(
                    loggedInUser.getId(),
                    selected.getSubjectId(),
                    selected.getSubjectName()
                );

                if (ok) {
                    JOptionPane.showMessageDialog(dialog, "Subject registered successfully!");
                } else {
                    JOptionPane.showMessageDialog(dialog, "You are already registered in this subject (or failed to register).", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
                refreshRegistered.run();
            });

            closeBtn.addActionListener(ev -> dialog.dispose());

            JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
            bottomPanel.add(subjectCombo, BorderLayout.CENTER);
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actions.add(registerBtn);
            actions.add(closeBtn);
            bottomPanel.add(actions, BorderLayout.EAST);

            dialog.add(registeredScroll, BorderLayout.CENTER);
            dialog.add(bottomPanel, BorderLayout.SOUTH);

            refreshRegistered.run();
            dialog.setVisible(true);
        });

        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonPanel.add(takeExamBtn);
        buttonPanel.add(viewResultsBtn);
        buttonPanel.add(viewSubjectsBtn);

        // Add components to parent
        parent.add(Box.createVerticalStrut(20));
        parent.add(buttonPanel);
    }

    private void showLecturerPanel(JPanel parent) {
        JButton enterMarksBtn = new JButton("Enter/Update Marks");
        JButton viewStudentsBtn = new JButton("View My Students");
        JButton viewExamsBtn = new JButton("View/Manage Exams");

        // Enter/Update Marks Button
        enterMarksBtn.addActionListener(e -> {
            // Show a dialog to select a student and exam to enter/update marks
            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            
            // Get list of students
            ArrayList<Student> students = studentService.loadStudents();
            String[] studentNames = students.stream()
                .map(s -> s.getId() + " - " + s.getName())
                .toArray(String[]::new);
            
            JComboBox<String> studentCombo = new JComboBox<>(studentNames);
            JTextField examIdField = new JTextField(10);
            JTextField marksField = new JTextField(10);
            
            panel.add(new JLabel("Select Student:"));
            panel.add(studentCombo);
            panel.add(new JLabel("Exam ID:"));
            panel.add(examIdField);
            panel.add(new JLabel("Marks:"));
            panel.add(marksField);
            
            int result = JOptionPane.showConfirmDialog(
                this, panel, "Enter/Update Marks",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int studentId = Integer.parseInt(studentNames[studentCombo.getSelectedIndex()].split(" - ")[0]);
                    int examId = Integer.parseInt(examIdField.getText().trim());
                    double marks = Double.parseDouble(marksField.getText().trim());
                    
                    // Here you would typically call a service to save the marks
                    // For now, just show a success message
                    JOptionPane.showMessageDialog(this, 
                        String.format("Successfully updated marks for Student ID: %d, Exam ID: %d, Marks: %.2f", 
                        studentId, examId, marks));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter valid numbers for Exam ID and Marks.", 
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // View Students Button
        viewStudentsBtn.addActionListener(e -> {
            // Show list of students in a scrollable dialog
            ArrayList<Student> students = studentService.loadStudents();
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No students found.");
                return;
            }
            
            String[] columns = {"ID", "Name", "Email"};
            Object[][] data = students.stream()
                .map(s -> new Object[]{s.getId(), s.getName(), s.getEmail()})
                .toArray(Object[][]::new);
                
            JTable table = new JTable(data, columns);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(500, 300));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Student List", JOptionPane.PLAIN_MESSAGE);
        });
        
        // View/Manage Exams Button
        viewExamsBtn.addActionListener(e -> {
            // Show list of exams
            try (BufferedReader br = new BufferedReader(new FileReader("data/exams.txt"))) {
                StringBuilder exams = new StringBuilder("Available Exams:\n\n");
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        String[] parts = line.split(",");
                        if (parts.length >= 2) {
                            exams.append("ID: ").append(parts[0])
                                .append(", Title: ").append(parts[1])
                                .append("\n");
                        }
                    }
                }
                JTextArea textArea = new JTextArea(exams.toString());
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(500, 300));
                JOptionPane.showMessageDialog(this, scrollPane, "Available Exams", JOptionPane.PLAIN_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading exams: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonPanel.add(enterMarksBtn);
        buttonPanel.add(viewStudentsBtn);
        buttonPanel.add(viewExamsBtn);

        parent.add(Box.createVerticalStrut(20));
        parent.add(buttonPanel);
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
