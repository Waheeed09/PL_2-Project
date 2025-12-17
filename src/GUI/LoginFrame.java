package GUI;

import javax.swing.*;
import java.awt.*;
import services.UserService;
import models.User;

public class LoginFrame extends JFrame {

    private UserService userService;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;

    public LoginFrame(UserService userService) {
        this.userService = userService;
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
        btnLogin.addActionListener(e -> {
            String email = txtEmail.getText();
            String pass = new String(txtPassword.getPassword());

            User user = userService.login(email, pass);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "Welcome " + user.getName());
                this.dispose();
                // توجه حسب الدور
                switch (user.getRole()) {
                    case "admin":
                        new AdminDashboard(user).setVisible(true);
                        break;
                    case "student":
                        new StudentDashboard(user).setVisible(true);
                        break;
                    case "lecturer":
                        new LecturerDashboard(user).setVisible(true);
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });

        btnRegister.addActionListener(e -> {
            new RegisterFrame(userService).setVisible(true);
            this.dispose();
        });
    }
}
