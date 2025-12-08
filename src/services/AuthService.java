package services;

import models.Student;
import models.User;

import java.util.List;

public class AuthService {
    private static final String STUDENTS_FILE = "students.txt";
    private static User currentUser = null;
    
    /**
     * Login as Student
     */
    public static Student loginStudent(String studentId, String password) {
        List<String> lines = FileHandler.readFile(STUDENTS_FILE);
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 4 && parts[0].equals(studentId)) {
                if (parts[2].equals(password)) {
                    Student student = Student.fromFileString(line);
                    currentUser = student;
                    System.out.println("Student logged in: " + student.getName());
                    return student;
                } else {
                    System.out.println("Incorrect password");
                    return null;
                }
            }
        }
        
        System.out.println("Student ID not found");
        return null;
    }
    
    /**
     * Register new student (for testing purposes)
     */
    public static boolean registerStudent(String name, String password, String email) {
        String studentId = FileHandler.generateId(STUDENTS_FILE, "S");
        Student student = new Student(studentId, name, password, email);
        
        return FileHandler.appendToFile(STUDENTS_FILE, student.toFileString());
    }
    
    /**
     * Update student password
     */
    public static boolean updatePassword(String studentId, String oldPassword, String newPassword) {
        List<String> lines = FileHandler.readFile(STUDENTS_FILE);
        
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split("\\|");
            
            if (parts.length >= 4 && parts[0].equals(studentId)) {
                if (parts[2].equals(oldPassword)) {
                    Student student = Student.fromFileString(line);
                    student.setPassword(newPassword);
                    
                    return FileHandler.updateLine(STUDENTS_FILE, line, student.toFileString());
                } else {
                    System.out.println("Incorrect old password");
                    return false;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Update student information
     */
    public static boolean updateStudentInfo(String studentId, String newName, String newEmail) {
        List<String> lines = FileHandler.readFile(STUDENTS_FILE);
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            
            if (parts.length >= 4 && parts[0].equals(studentId)) {
                Student student = Student.fromFileString(line);
                student.setName(newName);
                student.setEmail(newEmail);
                
                // Update current user if logged in
                if (currentUser != null && currentUser.getUserId().equals(studentId)) {
                    currentUser = student;
                }
                
                return FileHandler.updateLine(STUDENTS_FILE, line, student.toFileString());
            }
        }
        
        return false;
    }
    
    /**
     * Logout current user
     */
    public static void logout() {
        if (currentUser != null) {
            System.out.println("User logged out: " + currentUser.getName());
            currentUser = null;
        }
    }
    
    /**
     * Get current logged-in user
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if user is logged in
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Get current student (type-safe)
     */
    public static Student getCurrentStudent() {
        if (currentUser instanceof Student) {
            return (Student) currentUser;
        }
        return null;
    }
    
    /**
     * Verify student authorization for specific action
     */
    public static boolean hasPermission(String action) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.hasPermission(action);
    }
}