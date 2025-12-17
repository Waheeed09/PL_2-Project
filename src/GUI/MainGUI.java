package GUI;

import models.*;
import services.*;
import services.FileManager;
import javax.swing.*;
import java.awt.*;
import java.util.List;

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

    private void showAdminPanel(JPanel parent) {
        JButton manageUsersBtn = new JButton("Manage Users");
        JButton manageSubjectsBtn = new JButton("Manage Subjects");
        JButton viewResultsBtn = new JButton("View Results");

        manageUsersBtn.addActionListener(e -> {
            // TODO: Implement manage users functionality
            JOptionPane.showMessageDialog(this, "Manage Users functionality will be implemented here");
        });

        manageSubjectsBtn.addActionListener(e -> {
            // TODO: Implement manage subjects functionality
            JOptionPane.showMessageDialog(this, "Manage Subjects functionality will be implemented here");
        });

        viewResultsBtn.addActionListener(e -> {
            // TODO: Implement view results functionality
            JOptionPane.showMessageDialog(this, "View Results functionality will be implemented here");
        });

        // Add components to panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonPanel.add(manageUsersBtn);
        buttonPanel.add(manageSubjectsBtn);
        buttonPanel.add(viewResultsBtn);

        parent.add(Box.createVerticalStrut(20));
        parent.add(buttonPanel);
    }

    private void showStudentPanel(JPanel parent) {
        JButton takeExamBtn = new JButton("Take Exam");
        JButton viewResultsBtn = new JButton("View My Results");
        JButton viewSubjectsBtn = new JButton("View My Subjects");

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
        JButton enterMarksBtn = new JButton("Enter Marks");
        JButton viewStudentsBtn = new JButton("View Students");

        enterMarksBtn.addActionListener(e -> {
            // TODO: Implement enter marks functionality
            JOptionPane.showMessageDialog(this, "Enter marks functionality will be implemented here");
        });

        viewStudentsBtn.addActionListener(e -> {
            // TODO: Implement view students functionality
            JOptionPane.showMessageDialog(this, "Student list will be displayed here");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));
        buttonPanel.add(enterMarksBtn);
        buttonPanel.add(viewStudentsBtn);

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
