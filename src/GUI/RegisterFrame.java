package GUI;

import javax.swing.*;
import java.awt.*;
import services.UserService;
import models.User;

public class RegisterFrame extends JFrame {

    private UserService userService;
    private JTextField txtName, txtEmail, txtId;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnRegister, btnBack;

    public RegisterFrame(UserService userService) {
        this.userService = userService;
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
        btnRegister.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String name = txtName.getText();
                String email = txtEmail.getText();
                String pass = new String(txtPassword.getPassword());
                String role = cmbRole.getSelectedItem().toString();

                User newUser = new User(id, name, email, pass, role);
                boolean success = userService.register(newUser);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Registration successful!");
                    new LoginFrame(userService).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Email already in use.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format.");
            }
        });

        btnBack.addActionListener(e -> {
            new LoginFrame(userService).setVisible(true);
            this.dispose();
        });
    }
}
