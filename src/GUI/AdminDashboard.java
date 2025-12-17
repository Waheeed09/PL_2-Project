package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import models.User;
import models.Student;
import models.Lecturer;
import services.UserService;

public class AdminDashboard extends JFrame {

    private User admin;
    private UserService userService;

    private DefaultListModel<String> userListModel;
    private JList<String> userList;
    private List<User> users;

    // ======= الكونستركتور =======
    public AdminDashboard(User admin) {
        this.admin = admin;
        this.userService = new UserService(new ArrayList<>()); // users list empty initially
        this.users = userService.getAllUsers(); // قائمة المستخدمين الفعلية

        setTitle("Admin Dashboard - " + admin.getName());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        refreshUserList();

        JScrollPane scrollPane = new JScrollPane(userList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 5));

        JButton btnAddUser = new JButton("Add User");
        JButton btnModifyUser = new JButton("Modify User");
        JButton btnDeleteUser = new JButton("Delete User");
        JButton btnRefresh = new JButton("Refresh List");

        buttonPanel.add(btnAddUser);
        buttonPanel.add(btnModifyUser);
        buttonPanel.add(btnDeleteUser);
        buttonPanel.add(btnRefresh);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Actions
        btnAddUser.addActionListener(e -> addUser());
        btnModifyUser.addActionListener(e -> modifyUser());
        btnDeleteUser.addActionListener(e -> deleteUser());
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
}
