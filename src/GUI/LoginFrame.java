package GUI;

import javax.swing.*;
import java.awt.*;
import services.UserService;
import services.StudentService;
import services.LecturerService;
import models.User;
import models.Student;
import models.Lecturer;

public class LoginFrame extends JFrame {

    private UserService userService;
    private StudentService studentService;
    private LecturerService lecturerService;

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;

    public LoginFrame(UserService userService, StudentService studentService, LecturerService lecturerService) {
        this.userService = userService;
        this.studentService = studentService;
        this.lecturerService = lecturerService;

        setTitle("Login");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        panel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panel.add(txtEmail);

        panel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);

        btnLogin = new JButton("Login");
        btnRegister = new JButton("Register");

        panel.add(btnLogin);
        panel.add(btnRegister);

        add(panel);

        // Action listeners
        btnLogin.addActionListener(e -> loginAction());
        btnRegister.addActionListener(e -> registerAction());
    }

    private void loginAction() {
        String email = txtEmail.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        User user = userService.login(email, pass);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Welcome " + user.getName());
            this.dispose();

            switch (user.getRole()) {
                case "admin":
                    new AdminDashboard(user, studentService, userService).setVisible(true);
                    break;
                case "student":
                    if (user instanceof Student) {
                        new StudentDashboard((Student) user, studentService).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "User is not a valid student.");
                    }
                    break;
                case "lecturer":
                    if (user instanceof Lecturer) {
                        new LecturerDashboard((Lecturer) user, lecturerService).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "User is not a valid lecturer.");
                    }
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Unknown role");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials");
        }
    }

    private void registerAction() {
        new RegisterFrame(userService, studentService, lecturerService).setVisible(true);
        this.dispose();
    }
}
