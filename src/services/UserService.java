package services;

<<<<<<< HEAD
import java.util.List;
import models.User;

public class UserService {

    private List<User> users;

    public UserService(List<User> users) {
        this.users = users;
    }

    // Authenticate user login
    public User login(String email, String password) {
        for (User user : users) {
            if (user.login(email, password)) {
                return user;
=======
import java.util.ArrayList;

public class UserService {

    private ArrayList<User> users = new ArrayList<>();

    public UserService() {

        users.add(new Admin(1, "Admin", "admin@mail.com", "admin", "1234"));
        users.add(new Student(2,"IBRAHIM","IB515@mail.com","St","0007"));
    }

    public User login(String username, String password) {
        for (User u : users) {
            if (u.getUsername() != null &&
                    u.getUsername().equals(username) &&
                    u.getPassword().equals(password)) {
                return u;
>>>>>>> 6cc7963319aaf4ed613e38088c217b350fe09652
            }
        }
        return null;
    }

<<<<<<< HEAD
    // Register a new user
    public boolean register(User newUser) {
        // Check if email already exists
        for (User user : users) {
            if (user.getEmail().equals(newUser.getEmail())) {
                return false; // Email already in use
            }
        }
        users.add(newUser);
        return true;
    }

    // Update user profile
    public boolean updateProfile(int userId, String newName, String newEmail, String newPassword) {
        for (User user : users) {
            if (user.getId() == userId) {
                if (newName != null) user.setName(newName);
                if (newEmail != null) user.setEmail(newEmail);
                if (newPassword != null) user.setPassword(newPassword);
                return true;
            }
        }
        return false; // User not found
    }

    // Change password
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        for (User user : users) {
            if (user.getId() == userId && user.getPassword().equals(oldPassword)) {
                user.setPassword(newPassword);
                return true;
            }
        }
        return false; // Invalid user or old password
    }

    // Get user by ID
    public User getUserById(int userId) {
        for (User user : users) {
            if (user.getId() == userId) {
                return user;
            }
        }
        return null;
    }

    // Get all users
    public List<User> getAllUsers() {
        return users;
    }
=======
>>>>>>> 6cc7963319aaf4ed613e38088c217b350fe09652
}






