package GUI;

import java.awt.*;
import javax.swing.*;
import models.Lecturer;
import models.Student;
import models.User;
import services.LecturerService;
import services.StudentService;
import services.UserService;

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
        setSize(520, 600);
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

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setOpaque(false);

        JLabel idLabel = new JLabel("ID:"); idLabel.setFont(UITheme.FONT_LABEL); idLabel.setForeground(UITheme.TEXT_COLOR);
        form.add(idLabel);
        txtId = new JTextField(); txtId.setFont(UITheme.FONT_BODY); form.add(txtId);

        JLabel nameLabel = new JLabel("Name:"); nameLabel.setFont(UITheme.FONT_LABEL); nameLabel.setForeground(UITheme.TEXT_COLOR);
        form.add(nameLabel);
        txtName = new JTextField(); txtName.setFont(UITheme.FONT_BODY); form.add(txtName);

        JLabel emailLabel = new JLabel("Email:"); emailLabel.setFont(UITheme.FONT_LABEL); emailLabel.setForeground(UITheme.TEXT_COLOR);
        form.add(emailLabel);
        txtEmail = new JTextField(); txtEmail.setFont(UITheme.FONT_BODY); form.add(txtEmail);

        JLabel passLabel = new JLabel("Password:"); passLabel.setFont(UITheme.FONT_LABEL); passLabel.setForeground(UITheme.TEXT_COLOR);
        form.add(passLabel);
        txtPassword = new JPasswordField(); txtPassword.setFont(UITheme.FONT_BODY); form.add(txtPassword);

        JLabel roleLabel = new JLabel("Role:"); roleLabel.setFont(UITheme.FONT_LABEL); roleLabel.setForeground(UITheme.TEXT_COLOR);
        form.add(roleLabel);
        cmbRole = new JComboBox<>(new String[]{"admin", "student", "lecturer"}); cmbRole.setFont(UITheme.FONT_BODY); form.add(cmbRole);

        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 2;
        background.add(form, gbc);

        btnRegister = new RoundedButton("✓ REGISTER NOW", UITheme.createLetterIcon("+", new Color(0, 150, 136), Color.WHITE, 26));
        btnRegister.setBackground(new Color(0, 150, 136)); // Bright teal green
        btnRegister.setFont(new Font("Arial", Font.BOLD, 15));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setPreferredSize(new Dimension(200, 55));
        btnRegister.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        
        btnBack = new RoundedButton("← Back", UITheme.createLetterIcon("<", new Color(150, 150, 150), Color.WHITE, 18));
        btnBack.setBackground(new Color(150, 150, 150));
        btnBack.setFont(new Font("Arial", Font.BOLD, 12));
        btnBack.setForeground(Color.WHITE);
        btnBack.setPreferredSize(new Dimension(130, 50));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnRow.setOpaque(false);
        btnRow.add(btnRegister);
        btnRow.add(btnBack);

        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 10, 10);
        background.add(btnRow, gbc);

        setContentPane(background);

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
