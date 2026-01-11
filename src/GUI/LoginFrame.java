package GUI;

import java.awt.*;
import javax.swing.*;
import models.Lecturer;
import models.Student;
import models.User;
import services.LecturerService;
import services.StudentService;
import services.UserService;

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
        setSize(420, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        // Background panel that paints the application background image
        String bgPath = "D:\\Uni_Bedo\\Pl2\\Project_pl2\\PL_2-Project\\resources\\WhatsApp Image 2026-01-11 at 4.44.40 PM.jpeg";
        Image bg = UITheme.loadBackgroundImageAbsolute(bgPath);
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) {
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        background.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel systemLabel = new JLabel("University Examination System", JLabel.CENTER);
        systemLabel.setFont(UITheme.FONT_TITLE);
        systemLabel.setForeground(UITheme.TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        background.add(systemLabel, gbc);

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setOpaque(false);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(UITheme.FONT_LABEL);
        emailLabel.setForeground(UITheme.TEXT_COLOR);
        form.add(emailLabel);
        txtEmail = new JTextField();
        txtEmail.setFont(UITheme.FONT_BODY);
        form.add(txtEmail);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(UITheme.FONT_LABEL);
        passLabel.setForeground(UITheme.TEXT_COLOR);
        form.add(passLabel);
        txtPassword = new JPasswordField();
        txtPassword.setFont(UITheme.FONT_BODY);
        form.add(txtPassword);

        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 2;
        background.add(form, gbc);

        btnLogin = new RoundedButton("Login", UITheme.createLetterIcon("IN", UITheme.PRIMARY_COLOR, Color.WHITE, 22));
        btnLogin.setBackground(UITheme.PRIMARY_COLOR);

        btnRegister = new RoundedButton("Register", UITheme.createLetterIcon("+", UITheme.PRIMARY_COLOR.darker(), Color.WHITE, 22));
        btnRegister.setBackground(UITheme.PRIMARY_COLOR.darker());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        btnRow.setOpaque(false);
        btnRow.add(btnLogin);
        btnRow.add(btnRegister);

        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        background.add(btnRow, gbc);

        setContentPane(background);

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
