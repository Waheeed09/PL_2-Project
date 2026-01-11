package GUI;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.Lecturer;
import models.Student;
import models.Subject;
import models.User;
import services.FileManager;
import services.StudentService;
import services.UserService;

public class AdminDashboard extends JFrame {

    private User admin;
    private StudentService studentService;
    private UserService userService;

    private DefaultListModel<String> userListModel;
    private JList<String> userList;
    private List<User> users;

    public AdminDashboard(User admin, StudentService studentService, UserService userService) {
        this.admin = admin;
        this.studentService = studentService;
        this.userService = userService;
        this.users = userService.getAllUsers();

        setTitle("Admin Dashboard - " + admin.getName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setFont(UITheme.FONT_BODY);
        refreshUserList();

        JScrollPane scrollPane = new JScrollPane(userList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 8, 8));
        buttonPanel.setOpaque(false);

        JButton btnAddUser = new JButton("Add User"); UITheme.styleButton(btnAddUser);
        JButton btnModifyUser = new JButton("Modify User"); UITheme.styleButton(btnModifyUser);
        JButton btnDeleteUser = new JButton("Delete User"); UITheme.styleButton(btnDeleteUser);
        JButton btnRefresh = new JButton("Refresh List"); UITheme.styleButton(btnRefresh);
        JButton btnAssignSubject = new JButton("Assign Subject"); UITheme.styleButton(btnAssignSubject);
        JButton btnUpdateStudent = new JButton("Update Student"); UITheme.styleButton(btnUpdateStudent);

        buttonPanel.add(btnAddUser);
        buttonPanel.add(btnModifyUser);
        buttonPanel.add(btnDeleteUser);
        buttonPanel.add(btnAssignSubject);
        buttonPanel.add(btnUpdateStudent);
        buttonPanel.add(btnRefresh);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        background.add(mainPanel, BorderLayout.CENTER);
        setContentPane(background);

        // Actions
        btnAddUser.addActionListener(e -> addUser());
        btnModifyUser.addActionListener(e -> modifyUser());
        btnDeleteUser.addActionListener(e -> deleteUser());
        btnAssignSubject.addActionListener(e -> assignSubject());
        btnUpdateStudent.addActionListener(e -> updateStudent());
        btnRefresh.addActionListener(e -> refreshUserList());
    }

    private void refreshUserList() {
        userListModel.clear();
        for (User u : users) {
            userListModel.addElement(u.getId() + " - " + u.getName() + " (" + u.getRole() + ")");
        }
    }

    private void addUser() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField passwordField = new JTextField();
        String[] roles = {"student", "lecturer"};
        JComboBox<String> roleBox = new JComboBox<>(roles);

        Object[] message = {
                "ID:", idField,
                "Name:", nameField,
                "Email:", emailField,
                "Password:", passwordField,
                "Role:", roleBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String password = passwordField.getText().trim();
                String role = (String) roleBox.getSelectedItem();

                User newUser;
                if ("student".equals(role)) {
                    newUser = new Student(id, name, email, password);
                } else {
                    newUser = new Lecturer(id, name, email, password);
                }

                boolean added = userService.register(newUser);
                if (added) {
                    users.add(newUser);
                    refreshUserList();
                    JOptionPane.showMessageDialog(this, "User added successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Email already exists!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
            }
        }
    }

    private void modifyUser() {
        int index = userList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to modify.");
            return;
        }
        User user = users.get(index);

        JTextField nameField = new JTextField(user.getName());
        JTextField emailField = new JTextField(user.getEmail());
        JTextField passwordField = new JTextField(user.getPassword());

        Object[] message = {
                "Name:", nameField,
                "Email:", emailField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Modify User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            userService.updateProfile(user.getId(), nameField.getText().trim(), emailField.getText().trim(), passwordField.getText().trim());
            refreshUserList();
            JOptionPane.showMessageDialog(this, "User updated successfully!");
        }
    }

    private void deleteUser() {
        int index = userList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to delete.");
            return;
        }
        User user = users.get(index);

        int confirm = JOptionPane.showConfirmDialog(this, "Delete user: " + user.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            users.remove(index);
            refreshUserList();
            JOptionPane.showMessageDialog(this, "User deleted successfully!");
        }
    }

    // ==================== Assign Subject to Student ====================
    private void assignSubject() {
        int index = userList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Select a student to assign subject.");
            return;
        }
        User user = users.get(index);
        
        // Check if user is a student
        if (!user.getRole().equals("student")) {
            JOptionPane.showMessageDialog(this, "Please select a student!");
            return;
        }

        Student student = (Student) user;
        
        // Get all available subjects
        ArrayList<Subject> subjects = studentService.loadAllSubjects();
        if (subjects.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No subjects available!");
            return;
        }

        // Create subject selection dialog
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel labelInfo = new JLabel("📚 Select subjects for: " + student.getName());
        labelInfo.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(labelInfo, BorderLayout.NORTH);

        // Subject checkboxes
        JPanel subjectPanel = new JPanel(new BorderLayout());
        subjectPanel.setBorder(BorderFactory.createTitledBorder("Available Subjects"));
        
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        
        javax.swing.JCheckBox[] subjectCheckboxes = new javax.swing.JCheckBox[subjects.size()];
        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);
            subjectCheckboxes[i] = new javax.swing.JCheckBox(subject.getSubjectId() + " - " + subject.getSubjectName());
            checkboxPanel.add(subjectCheckboxes[i]);
        }
        
        JScrollPane scrollPane = new JScrollPane(checkboxPanel);
        subjectPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(subjectPanel, BorderLayout.CENTER);

        // Show dialog
        int result = JOptionPane.showConfirmDialog(this, panel, "✏️ Assign Subjects", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            int assigned = 0;
            for (int i = 0; i < subjectCheckboxes.length; i++) {
                if (subjectCheckboxes[i].isSelected()) {
                    Subject subject = subjects.get(i);
                    if (studentService.registerSubject(student.getId(), subject.getSubjectId(), subject.getSubjectName())) {
                        assigned++;
                    }
                }
            }
            if (assigned > 0) {
                JOptionPane.showMessageDialog(this, "✅ " + assigned + " subject(s) assigned successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "❌ No subjects were assigned.");
            }
        }
    }

    // ==================== Update Student ====================
    private void updateStudent() {
        int index = userList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Select a student to update.");
            return;
        }
        User user = users.get(index);
        
        // Check if user is a student
        if (!user.getRole().equals("student")) {
            JOptionPane.showMessageDialog(this, "Please select a student!");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel idLabel = new JLabel(String.valueOf(user.getId()));
        idLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField nameField = new JTextField(user.getName(), 20);
        JTextField emailField = new JTextField(user.getEmail(), 20);
        JPasswordField passwordField = new JPasswordField(user.getPassword(), 20);

        panel.add(new JLabel("🆔 Student ID:"));
        panel.add(idLabel);
        panel.add(new JLabel("📝 Name:"));
        panel.add(nameField);
        panel.add(new JLabel("📧 Email:"));
        panel.add(emailField);
        panel.add(new JLabel("🔐 Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "✏️ Update Student", JOptionPane.OK_CANCEL_OPTION);
        
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
            FileManager.saveUsers(users);
            refreshUserList();
            JOptionPane.showMessageDialog(this, "✅ Student updated successfully!");
        }
    }
}
