import java.util.Scanner;
import models.*;
import services.*;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        AdminService adminService = new AdminService();

        // ===== Load data =====
        adminService.setUsers(FileManager.loadUsers());
        adminService.setSubjects(FileManager.loadSubjects());
        adminService.setResults(FileManager.loadResults());

        // ===== Ensure default admin =====
        boolean adminExists = false;
        for (User u : adminService.getUsers()) {
            if ("admin".equals(u.getRole())) {
                adminExists = true;
                break;
            }
        }

        if (!adminExists) {
            Admin admin = new Admin(1, "Admin", "admin@system.com", "1234");
            adminService.addUser(admin);
            FileManager.saveUsers(adminService.getUsers());
            System.out.println("✔ Default admin created");
        }

        // ===== Main Menu =====
        boolean exit = false;
        while (!exit) {
            System.out.println("\n===== SYSTEM =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    User user = login(sc, adminService);
                    if (user != null) {
                        System.out.println("Welcome " + user.getName() + " (" + user.getRole() + ")");
                    } else {
                        System.out.println("Login failed");
                    }
                    break;

                case "2":
                    register(sc, adminService);
                    break;

                case "3":
                    exit = true;
                    break;

                default:
                    System.out.println("Invalid choice");
            }
        }

        // ===== Save before exit =====
        FileManager.saveUsers(adminService.getUsers());
        FileManager.saveSubjects(adminService.getSubjects());
        FileManager.saveResults(adminService.getResults());

        sc.close();
        System.out.println("System closed.");
    }

    // ================= Login =================
    private static User login(Scanner sc, AdminService adminService) {
        System.out.println("\n--- Login ---");
        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        for (User u : adminService.getUsers()) {
            if (u.login(email, password)) {
                return u;
            }
        }
        return null;
    }

    // ================= Register =================
    private static void register(Scanner sc, AdminService adminService) {

        System.out.println("\n--- Register ---");

        System.out.print("ID: ");
        int id = Integer.parseInt(sc.nextLine());

        System.out.print("Name: ");
        String name = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        System.out.println("Role?");
        System.out.println("1. Admin");
        System.out.println("2. Lecturer");
        System.out.println("3. Student");
        System.out.print("Choice: ");
        String roleChoice = sc.nextLine();

        String role;
        switch (roleChoice) {
            case "1": role = "admin"; break;
            case "2": role = "lecturer"; break;
            case "3": role = "student"; break;
            default:
                System.out.println("Invalid role");
                return;
        }

        User user;

        if (role.equals("admin")) {
            user = new Admin(id, name, email, password);
        } else {
            user = new User(id, name, email, password, role);
        }

        adminService.addUser(user);

        // ✅ Save immediately
        FileManager.saveUsers(adminService.getUsers());

        System.out.println("Registered successfully and saved");
    }
}
