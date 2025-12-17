package GUI;

import javax.swing.*;
import java.awt.*;
import services.UserService;
import services.StudentService;
import services.LecturerService;
import models.User;
import models.Student;
import models.Lecturer;

public class RegisterFrame extends JFrame {

    private UserService userService;
    private StudentService studentService;
    private LecturerService lecturerService;

    private JTextField txtName, txtEmail, txtId;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnRegister, btnBack;

    public RegisterFrame(UserService userService, StudentService studentService, LecturerService lecturerService) {
        this.userService = userService;
        this.studentService = studentService;
        this.lecturerService = lecturerService;

        setTitle("Register");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));

        panel.add(new JLabel("ID:"));
        txtId = new JTextField();
        panel.add(txtId);

        panel.add(new JLabel("Name:"));
        txtName = new JTextField();
        panel.add(txtName);

        panel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panel.add(txtEmail);

        panel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);

        panel.add(new JLabel("Role:"));
        cmbRole = new JComboBox<>(new String[]{"admin", "student", "lecturer"});
        panel.add(cmbRole);

        btnRegister = new JButton("Register");
        btnBack = new JButton("Back");
        panel.add(btnRegister);
        panel.add(btnBack);

        add(panel);

        // Action listeners
        btnRegister.addActionListener(e -> registerAction());
        btnBack.addActionListener(e -> backAction());
    }

    private void registerAction() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String pass = new String(txtPassword.getPassword()).trim();
            String role = cmbRole.getSelectedItem().toString();

            User newUser;

            switch (role) {
                case "student":
                    newUser = new Student(id, name, email, pass);
                    break;
                case "lecturer":
                    newUser = new Lecturer(id, name, email, pass);
                    break;
                case "admin":
                    newUser = new User(id, name, email, pass, "admin");
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Invalid role.");
                    return;
            }

            boolean success = userService.register(newUser);
            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
                this.dispose();

                // Open dashboard based on role
                switch (role) {
                    case "admin":
                        new AdminDashboard(newUser, studentService, userService).setVisible(true);
                        break;
                    case "student":
                        new StudentDashboard((Student) newUser, studentService).setVisible(true);
                        break;
                    case "lecturer":
                        new LecturerDashboard((Lecturer) newUser, lecturerService).setVisible(true);
                        break;
                }

            } else {
                JOptionPane.showMessageDialog(this, "Email already in use.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid ID format.");
        }
    }

    private void backAction() {
        new LoginFrame(userService, studentService, lecturerService).setVisible(true);
        this.dispose();
    }
}
