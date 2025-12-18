package GUI;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
        setSize(900, 700); // Increased size slightly for better admin view
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
        // Simple try-catch for files that might not exist yet
        try { adminService.setSubjects(FileManager.loadSubjects()); } catch (Exception e) {}
        try { adminService.setResults(FileManager.loadResults()); } catch (Exception e) {}
        try { if (FileManager.loadResults() != null) resultService.getAllResults().addAll(FileManager.loadResults()); } catch (Exception e) {}
        
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

        JLabel titleLabel = new JLabel("Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridy++; gbc.gridwidth = 1;
        panel.add(new JLabel("Email:"), gbc);
        JTextField emailField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridy++; gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            loggedInUser = userService.login(email, password);
            
            if (loggedInUser != null) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                showRoleBasedPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Login failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(loginButton, gbc);

        gbc.gridy++;
        JButton registerButton = new JButton("Create New Account");
        registerButton.addActionListener(e -> showRegistrationPanel());
        panel.add(registerButton, gbc);

        currentPanel = panel;
        add(currentPanel, BorderLayout.CENTER);
        revalidate(); repaint();
    }

    private void showRegistrationPanel() {
        if (currentPanel != null) remove(currentPanel);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Create New Account", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridy++; gbc.gridwidth = 1;
        panel.add(new JLabel("ID:"), gbc);
        JTextField idField = new JTextField(20);
        gbc.gridx = 1; panel.add(idField, gbc);

        gbc.gridy++; gbc.gridx = 0;
        panel.add(new JLabel("Name:"), gbc);
        JTextField nameField = new JTextField(20);
        gbc.gridx = 1; panel.add(nameField, gbc);

        gbc.gridy++; gbc.gridx = 0;
        panel.add(new JLabel("Email:"), gbc);
        JTextField emailField = new JTextField(20);
        gbc.gridx = 1; panel.add(emailField, gbc);

        gbc.gridy++; gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1; panel.add(passwordField, gbc);

        gbc.gridy++; gbc.gridx = 0;
        panel.add(new JLabel("Role:"), gbc);
        String[] roles = {"Student", "Lecturer", "Admin"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        gbc.gridx = 1; panel.add(roleCombo, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
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

        gbc.gridy++;
        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> showLoginPanel());
        panel.add(backButton, gbc);

        currentPanel = panel;
        add(currentPanel, BorderLayout.CENTER);
        revalidate(); repaint();
    }

    private void showRoleBasedPanel() {
        if (loggedInUser == null) { showLoginPanel(); return; }
        if (currentPanel != null) remove(currentPanel);

        JPanel panel = new JPanel(new BorderLayout());
        
        // Header with Logout
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel welcomeLabel = new JLabel("Welcome, " + loggedInUser.getName() + " (" + loggedInUser.getRole() + ")", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(Color.RED);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> {
            loggedInUser = null;
            showLoginPanel();
        });
        
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        switch (loggedInUser.getRole()) {
            case "admin":
                showAdminPanel(panel); // Passing main panel to add tabs directly
                break;
            case "student":
                showStudentPanel(contentPanel);
                panel.add(contentPanel, BorderLayout.CENTER);
                break;
            case "lecturer":
                showLecturerPanel(contentPanel);
                panel.add(contentPanel, BorderLayout.CENTER);
                break;
            default:
                contentPanel.add(new JLabel("Unknown role", JLabel.CENTER));
                panel.add(contentPanel, BorderLayout.CENTER);
        }

        currentPanel = panel;
        add(currentPanel, BorderLayout.CENTER);
        revalidate(); repaint();
    }

    // =========================================================================
    // ==================== ADMIN DASHBOARD (UPDATED) ==========================
    // =========================================================================
    
    // Implements Requirements:
    // a) User Authentication (Alter credentials)
    // b) User Management (CRUD + Search)
    // c) Subject Management (Assign to Students/Lecturers)
    // d) Grade Approval (Review and Publish)
    private void showAdminPanel(JPanel parentPanel) {
        JTabbedPane tabbedPane = new JTabbedPane();

        // --- Tab 1: User Management (Requirement b) ---
        JPanel userPanel = new JPanel(new BorderLayout());
        
        // Search Bar
        JPanel searchPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search User");
        JButton refreshBtn = new JButton("Refresh List");
        JButton addUserBtn = new JButton("Add New User");
        JButton updateUserBtn = new JButton("Update User");
        JButton deleteUserBtn = new JButton("Delete Selected");

        searchPanel.add(new JLabel("Search (Name/ID):"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);
        
        String[] columns = {"ID", "Name", "Email", "Role"};
        DefaultTableModel userModel = new DefaultTableModel(columns, 0);
        JTable userTable = new JTable(userModel);
        
        Runnable loadUsers = () -> {
            userModel.setRowCount(0);
            for (User u : adminService.getUsers()) {
                userModel.addRow(new Object[]{u.getId(), u.getName(), u.getEmail(), u.getRole()});
            }
        };
        loadUsers.run();

        // Search Logic
        searchBtn.addActionListener(e -> {
            String query = searchField.getText().toLowerCase();
            userModel.setRowCount(0);
            for (User u : adminService.getUsers()) {
                if (String.valueOf(u.getId()).contains(query) || u.getName().toLowerCase().contains(query)) {
                    userModel.addRow(new Object[]{u.getId(), u.getName(), u.getEmail(), u.getRole()});
                }
            }
        });
        
        refreshBtn.addActionListener(e -> { searchField.setText(""); loadUsers.run(); });

        // Add User Logic uses existing showUserForm method
        addUserBtn.addActionListener(e -> { showUserForm(null); loadUsers.run(); });
        
        // Update User Logic
        updateUserBtn.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row != -1) {
                int id = (int) userTable.getValueAt(row, 0);
                User userToUpdate = adminService.getUsers().stream()
                    .filter(u -> u.getId() == id)
                    .findFirst()
                    .orElse(null);
                if (userToUpdate != null) {
                    showUpdateUserDialog(userToUpdate);
                    loadUsers.run();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user.");
            }
        });
        
        // Delete Logic
        deleteUserBtn.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row != -1) {
                int id = (int) userTable.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete user ID: " + id + "?");
                if(confirm == JOptionPane.YES_OPTION){
                    adminService.getUsers().removeIf(u -> u.getId() == id);
                    FileManager.saveUsers(adminService.getUsers());
                    loadUsers.run();
                    JOptionPane.showMessageDialog(this, "User Deleted.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user.");
            }
        });

        JPanel userBottomPanel = new JPanel();
        userBottomPanel.add(addUserBtn);
        userBottomPanel.add(updateUserBtn);
        userBottomPanel.add(deleteUserBtn);

        userPanel.add(searchPanel, BorderLayout.NORTH);
        userPanel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        userPanel.add(userBottomPanel, BorderLayout.SOUTH);

        // --- Tab 2: Subject Management (Requirement c) ---
        JPanel subjectPanel = new JPanel(new BorderLayout());
        JPanel subControlPanel = new JPanel(new GridLayout(2, 1));
        JPanel subBtns1 = new JPanel();
        JPanel subBtns2 = new JPanel();

        JButton addSubjectBtn = new JButton("Add New Subject");
        JButton assignSubjectBtn = new JButton("Assign Subject to User");
        
        subBtns1.add(addSubjectBtn);
        subBtns2.add(assignSubjectBtn);
        subControlPanel.add(subBtns1);
        subControlPanel.add(subBtns2);

        JTextArea subjectListArea = new JTextArea();
        subjectListArea.setEditable(false);
        subjectListArea.setBorder(BorderFactory.createTitledBorder("Current Subjects & Assignments"));

        Runnable loadSubjects = () -> {
            subjectListArea.setText("");
            File sFile = new File("data/subjects.txt");
            if(sFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(sFile))) {
                    String line;
                    subjectListArea.append("--- Subjects ---\n");
                    while ((line = br.readLine()) != null) subjectListArea.append(line + "\n");
                } catch (Exception ex) {}
            }
            File aFile = new File("data/subject_assignments.txt");
            if(aFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(aFile))) {
                    String line;
                    subjectListArea.append("\n--- Assignments (SubjectID, UserID) ---\n");
                    while ((line = br.readLine()) != null) subjectListArea.append(line + "\n");
                } catch (Exception ex) {}
            }
        };
        loadSubjects.run();

        addSubjectBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog("Enter Subject ID:");
            String name = JOptionPane.showInputDialog("Enter Subject Name:");
            if (id != null && name != null && !id.isEmpty() && !name.isEmpty()) {
                try (PrintWriter pw = new PrintWriter(new FileWriter("data/subjects.txt", true))) {
                    pw.println(id + "," + name);
                    JOptionPane.showMessageDialog(this, "Subject Added.");
                    loadSubjects.run();
                } catch(Exception ex){}
            }
        });

        assignSubjectBtn.addActionListener(e -> {
            JPanel p = new JPanel(new GridLayout(2, 2, 5, 5));
            JTextField sId = new JTextField();
            JTextField uId = new JTextField();
            p.add(new JLabel("Subject ID:")); p.add(sId);
            p.add(new JLabel("User ID (Student/Lecturer):")); p.add(uId);
            int res = JOptionPane.showConfirmDialog(this, p, "Assign Subject", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION && !sId.getText().isEmpty() && !uId.getText().isEmpty()) {
                try (PrintWriter pw = new PrintWriter(new FileWriter("data/subject_assignments.txt", true))) {
                    pw.println(sId.getText() + "," + uId.getText());
                    JOptionPane.showMessageDialog(this, "Assigned Successfully!");
                    loadSubjects.run();
                } catch(Exception ex){}
            }
        });
        
        subjectPanel.add(new JScrollPane(subjectListArea), BorderLayout.CENTER);
        subjectPanel.add(subControlPanel, BorderLayout.SOUTH);

        // --- Tab 3: Grade Approval (Requirement d) ---
        JPanel gradePanel = new JPanel(new BorderLayout());
        DefaultTableModel gradeModel = new DefaultTableModel(new String[]{"Raw Data Line", "Status"}, 0);
        JTable gradeTable = new JTable(gradeModel);
        JButton loadGradesBtn = new JButton("Load Pending Grades");
        JButton approveBtn = new JButton("Approve & Publish All");
        approveBtn.setBackground(new Color(0, 153, 76)); // Green color
        approveBtn.setForeground(Color.WHITE);

        loadGradesBtn.addActionListener(e -> {
            gradeModel.setRowCount(0);
            File f = new File("data/results.txt");
            if (f.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String status = line.contains("APPROVED") ? "Published" : "PENDING REVIEW";
                        gradeModel.addRow(new Object[]{line, status});
                    }
                } catch(Exception ex){}
            } else {
                JOptionPane.showMessageDialog(this, "No results file found.");
            }
        });

        approveBtn.addActionListener(e -> {
            File f = new File("data/results.txt");
            if (!f.exists()) return;
            ArrayList<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.contains("APPROVED")) line += ",APPROVED";
                    lines.add(line);
                }
            } catch(Exception ex){}
            
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                for (String l : lines) pw.println(l);
                JOptionPane.showMessageDialog(this, "All Grades have been Approved & Published to Students!");
                loadGradesBtn.doClick(); // Refresh table
            } catch(Exception ex){
                JOptionPane.showMessageDialog(this, "Error saving file.");
            }
        });

        JPanel gradeBtnPanel = new JPanel();
        gradeBtnPanel.add(loadGradesBtn);
        gradeBtnPanel.add(approveBtn);

        gradePanel.add(new JScrollPane(gradeTable), BorderLayout.CENTER);
        gradePanel.add(gradeBtnPanel, BorderLayout.SOUTH);

        // --- Tab 4: My Profile (Requirement a - Alter credentials) ---
        JPanel profilePanel = new JPanel(new GridBagLayout());
        GridBagConstraints pGbc = new GridBagConstraints();
        pGbc.insets = new Insets(10, 10, 10, 10);
        pGbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameF = new JTextField(loggedInUser.getName(), 20);
        JTextField emailF = new JTextField(loggedInUser.getEmail(), 20);
        JPasswordField passF = new JPasswordField(loggedInUser.getPassword(), 20);
        JButton updateProfileBtn = new JButton("Update My Credentials");

        pGbc.gridx=0; pGbc.gridy=0; profilePanel.add(new JLabel("My Name:"), pGbc);
        pGbc.gridx=1; profilePanel.add(nameF, pGbc);
        
        pGbc.gridx=0; pGbc.gridy=1; profilePanel.add(new JLabel("My Email:"), pGbc);
        pGbc.gridx=1; profilePanel.add(emailF, pGbc);
        
        pGbc.gridx=0; pGbc.gridy=2; profilePanel.add(new JLabel("My Password:"), pGbc);
        pGbc.gridx=1; profilePanel.add(passF, pGbc);
        
        pGbc.gridx=0; pGbc.gridy=3; pGbc.gridwidth=2; 
        profilePanel.add(updateProfileBtn, pGbc);

        updateProfileBtn.addActionListener(e -> {
            loggedInUser.setName(nameF.getText());
            loggedInUser.setEmail(emailF.getText());
            loggedInUser.setPassword(new String(passF.getPassword()));
            
            // Update in the main list and save file
            for(int i=0; i<adminService.getUsers().size(); i++) {
                if(adminService.getUsers().get(i).getId() == loggedInUser.getId()) {
                    adminService.getUsers().set(i, loggedInUser);
                    break;
                }
            }
            FileManager.saveUsers(adminService.getUsers());
            JOptionPane.showMessageDialog(this, "Credentials Updated Successfully!");
            JLabel welcomeLabel = (JLabel)((JPanel)parentPanel.getComponent(0)).getComponent(0);
            welcomeLabel.setText("Welcome, " + loggedInUser.getName() + " (" + loggedInUser.getRole() + ")");
        });

        // Add Tabs
        tabbedPane.addTab("User Management", userPanel);
        tabbedPane.addTab("Subject Management", subjectPanel);
        tabbedPane.addTab("Grade Approval", gradePanel);
        tabbedPane.addTab("My Profile", profilePanel);

        parentPanel.add(tabbedPane, BorderLayout.CENTER);
    }
    
    // =========================================================================

    // Helper method to show user form for adding/editing users
    private void showUserForm(User user) {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"admin", "lecturer", "student"});
        
        if (user != null) {
            idField.setText(String.valueOf(user.getId()));
            idField.setEditable(false);
            nameField.setText(user.getName());
            emailField.setText(user.getEmail());
            passwordField.setText(user.getPassword());
            roleCombo.setSelectedItem(user.getRole());
        }
        
        panel.add(new JLabel("ID:")); panel.add(idField);
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Email:")); panel.add(emailField);
        panel.add(new JLabel("Password:")); panel.add(passwordField);
        panel.add(new JLabel("Role:")); panel.add(roleCombo);
        
        int result = JOptionPane.showConfirmDialog(
            this, panel, user == null ? "Add New User" : "Edit User",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
        if (result == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                String role = (String) roleCombo.getSelectedItem();
                
                User updatedUser = new User(id, name, email, password, role);
                if (user == null) {
                    adminService.addUser(updatedUser);
                    JOptionPane.showMessageDialog(this, "User added successfully!");
                }
                FileManager.saveUsers(adminService.getUsers());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid ID number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showStudentPanel(JPanel parent) {
        JButton takeExamBtn = new JButton("Take Exam");
        JButton viewResultsBtn = new JButton("View My Results");
        JButton viewSubjectsBtn = new JButton("View My Subjects");
        JButton feedbackBtn = new JButton("Give Feedback");
        JButton recorrectionBtn = new JButton("Request Recorrection");
        JButton updateProfileBtn = new JButton(" Update Profile");

        takeExamBtn.addActionListener(e -> {
            TakeExamForm takeExamForm = new TakeExamForm(loggedInUser.getId());
            takeExamForm.setVisible(true);
        });

        viewResultsBtn.addActionListener(e -> {
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

        viewSubjectsBtn.addActionListener(e -> {
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

        feedbackBtn.addActionListener(e -> showFeedbackDialog());

        recorrectionBtn.addActionListener(e -> {
            Student student = new Student(loggedInUser.getId(), loggedInUser.getName(), loggedInUser.getEmail(), loggedInUser.getPassword());
            new RecorrectionForm(student);
        });

        updateProfileBtn.addActionListener(e -> showUpdateStudentProfileDialog());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 1, 10, 10)); 
        buttonPanel.add(takeExamBtn);
        buttonPanel.add(viewResultsBtn);
        buttonPanel.add(viewSubjectsBtn);
        buttonPanel.add(feedbackBtn);
        buttonPanel.add(recorrectionBtn);
        buttonPanel.add(updateProfileBtn);

        parent.add(Box.createVerticalStrut(20));
        parent.add(buttonPanel);
    }

    private void showFeedbackDialog() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        inputPanel.add(new JLabel("Exam ID:"));
        JTextField examIdField = new JTextField();
        inputPanel.add(examIdField);
        
        JTextArea feedbackArea = new JTextArea(5, 20);
        feedbackArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(feedbackArea);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JLabel("Your Feedback:"), BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(this, panel, "Submit Feedback",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String examId = examIdField.getText().trim();
            String message = feedbackArea.getText().trim();
            if (examId.isEmpty() || message.isEmpty()) return;
            try {
                int examIdInt = Integer.parseInt(examId);
                FeedbackService.addFeedback(loggedInUser.getId(), examIdInt, message);
                JOptionPane.showMessageDialog(this, "Feedback submitted successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Exam ID must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==================== Update Student Profile ====================
    private void showUpdateStudentProfileDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel idLabel = new JLabel(String.valueOf(loggedInUser.getId()));
        idLabel.setFont(new Font("Arial", Font.BOLD, 12));
        JTextField nameField = new JTextField(loggedInUser.getName(), 20);
        JTextField emailField = new JTextField(loggedInUser.getEmail(), 20);
        JPasswordField passwordField = new JPasswordField(loggedInUser.getPassword(), 20);

        panel.add(new JLabel("🆔 Student ID (Read-Only):"));
        panel.add(idLabel);
        panel.add(new JLabel("📝 Full Name:"));
        panel.add(nameField);
        panel.add(new JLabel("📧 Email Address:"));
        panel.add(emailField);
        panel.add(new JLabel("🔐 Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "✏️ Update Student Profile", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (nameField.getText().isEmpty() || emailField.getText().isEmpty() || passwordField.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this, "❌ Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update user data
            loggedInUser.setName(nameField.getText().trim());
            loggedInUser.setEmail(emailField.getText().trim());
            loggedInUser.setPassword(new String(passwordField.getPassword()));

            // Update in users list
            for (int i = 0; i < adminService.getUsers().size(); i++) {
                if (adminService.getUsers().get(i).getId() == loggedInUser.getId()) {
                    adminService.getUsers().set(i, loggedInUser);
                    break;
                }
            }

            // Save data to file
            FileManager.saveUsers(adminService.getUsers());
            JOptionPane.showMessageDialog(this, "✅ Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

     private void showLecturerPanel(JPanel parent) {
        JButton createExamBtn = new JButton("Create Exam");
        JButton modifyExamBtn = new JButton("Modify Exam");
        JButton deleteExamBtn = new JButton("Delete Exam");
        JButton viewExamsBtn = new JButton("View Exams");
        JButton studentReportsBtn = new JButton("Student Reports");

        createExamBtn.addActionListener(e -> {
            // Open the CreateExamForm
            new CreateExamForm();
        });

        modifyExamBtn.addActionListener(e -> {
            String oldId = JOptionPane.showInputDialog(this, "Enter Exam ID to Modify:");
            if (oldId != null && !oldId.isEmpty()) modifyExamInFile(oldId);
        });

        deleteExamBtn.addActionListener(e -> {
            String delId = JOptionPane.showInputDialog(this, "Enter Exam ID to Delete:");
            if (delId != null && !delId.isEmpty()) deleteExamFromFile(delId);
        });

        viewExamsBtn.addActionListener(e -> {
            ArrayList<String> exams = loadExamsList();
            if (exams.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No exams found.");
            } else {
                JList<String> list = new JList<>(exams.toArray(new String[0]));
                JOptionPane.showMessageDialog(this, new JScrollPane(list), "All Exams", JOptionPane.PLAIN_MESSAGE);
            }
        });

        studentReportsBtn.addActionListener(e -> showStudentReports());

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.add(createExamBtn);
        buttonPanel.add(modifyExamBtn);
        buttonPanel.add(deleteExamBtn);
        buttonPanel.add(viewExamsBtn);
        buttonPanel.add(studentReportsBtn);

        parent.add(Box.createVerticalStrut(20));
        parent.add(buttonPanel);
    }

    // Helper Methods for Lecturer
    private void appendToExamsFile(String line) {
        try (FileWriter fw = new FileWriter("data/exams.txt", true); PrintWriter pw = new PrintWriter(fw)) {
            pw.println(line);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void modifyExamInFile(String targetId) {
        File inputFile = new File("data/exams.txt");
        File tempFile = new File("data/exams_temp.txt");
        boolean found = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(targetId)) {
                    found = true;
                    JTextField titleField = new JTextField(parts.length > 1 ? parts[1] : "");
                    JTextField subjectField = new JTextField(parts.length > 2 ? parts[2] : "");
                    JTextField durationField = new JTextField(parts.length > 3 ? parts[3] : "");
                    JPanel panel = new JPanel(new GridLayout(3, 2));
                    panel.add(new JLabel("New Title:")); panel.add(titleField);
                    panel.add(new JLabel("New Subject:")); panel.add(subjectField);
                    panel.add(new JLabel("New Duration:")); panel.add(durationField);
                    int result = JOptionPane.showConfirmDialog(this, panel, "Edit Exam " + targetId, JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        writer.println(targetId + "," + titleField.getText() + "," + subjectField.getText() + "," + durationField.getText());
                    } else {
                        writer.println(line);
                    }
                } else {
                    writer.println(line);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        if (found) { inputFile.delete(); tempFile.renameTo(inputFile); JOptionPane.showMessageDialog(this, "Exam updated successfully."); } 
        else { tempFile.delete(); JOptionPane.showMessageDialog(this, "Exam ID not found."); }
    }

    private void deleteExamFromFile(String targetId) {
        File inputFile = new File("data/exams.txt");
        File tempFile = new File("data/exams_temp.txt");
        boolean found = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(targetId)) { found = true; continue; }
                writer.println(line);
            }
        } catch (IOException e) { e.printStackTrace(); }
        if (found) { inputFile.delete(); tempFile.renameTo(inputFile); JOptionPane.showMessageDialog(this, "Exam deleted successfully."); }
        else { tempFile.delete(); JOptionPane.showMessageDialog(this, "Exam ID not found."); }
    }

    private ArrayList<String> loadExamsList() {
        ArrayList<String> list = new ArrayList<>();
        File file = new File("data/exams.txt");
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line; while ((line = br.readLine()) != null) list.add(line);
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    private void showStudentReports() {
        File file = new File("data/results.txt");
        if (!file.exists()) { JOptionPane.showMessageDialog(this, "No results found."); return; }
        ArrayList<String[]> dataList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line; while ((line = br.readLine()) != null) dataList.add(line.split(","));
        } catch (IOException e) { e.printStackTrace(); }
        String[] columns = {"Student ID", "Exam ID", "Score"};
        Object[][] data = new Object[dataList.size()][3];
        for (int i = 0; i < dataList.size(); i++) {
            String[] row = dataList.get(i);
            if (row.length >= 3) { data[i][0] = row[0]; data[i][1] = row[1]; data[i][2] = row[2]; }
        }
        JTable table = new JTable(data, columns);
        JOptionPane.showMessageDialog(this, new JScrollPane(table), "Student Reports", JOptionPane.PLAIN_MESSAGE);
    }

    // ==================== Update User Dialog ====================
    private void showUpdateUserDialog(User user) {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel idLabel = new JLabel(String.valueOf(user.getId()));
        idLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField nameField = new JTextField(user.getName(), 20);
        JTextField emailField = new JTextField(user.getEmail(), 20);
        JPasswordField passwordField = new JPasswordField(user.getPassword(), 20);

        panel.add(new JLabel("🆔 User ID:"));
        panel.add(idLabel);
        panel.add(new JLabel("📝 Name:"));
        panel.add(nameField);
        panel.add(new JLabel("📧 Email:"));
        panel.add(emailField);
        panel.add(new JLabel("🔐 Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "✏️ Update User", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty() || 
                passwordField.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this, "❌ All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update user
            user.setName(nameField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setPassword(new String(passwordField.getPassword()));

            // Save to file
            FileManager.saveUsers(adminService.getUsers());
            JOptionPane.showMessageDialog(this, "✅ User updated successfully!");
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) { e.printStackTrace(); }
        SwingUtilities.invokeLater(MainGUI::new);
    }
}