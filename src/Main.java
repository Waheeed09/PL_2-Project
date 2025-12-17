import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import models.Lecturer;
import models.Student;
import models.Subject;
import models.User;
import services.AdminService;
import services.FileManager;
import services.ResultService;
import services.StudentService;
import services.UserService;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AdminService adminService = new AdminService();
        StudentService studentService = new StudentService();
        ResultService resultService = new ResultService();
        UserService userService;

        // Ensure data directory exists
        new File("data").mkdirs();

        // Load data from files
        adminService.setUsers(FileManager.loadUsers());
        // Simple checks to avoid null pointers if files are empty/missing implementation
        try { adminService.setSubjects(FileManager.loadSubjects()); } catch (Exception e) {}
        try { adminService.setResults(FileManager.loadResults()); } catch (Exception e) {}
        try { 
            if (FileManager.loadResults() != null) {
                resultService.getAllResults().addAll(FileManager.loadResults());
            }
        } catch (Exception e) {}
        
        studentService.loadStudents();

        userService = new UserService(adminService.getUsers());

        // Login or Register
        User loggedInUser = null;
        while (loggedInUser == null) {
            System.out.println("\n===== Welcome to University System =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choice: ");
            String choice = sc.nextLine();

            if ("1".equals(choice)) {
                loggedInUser = login(sc, userService);
                if (loggedInUser == null) {
                    System.out.println("Login failed. Try again.");
                }
            } else if ("2".equals(choice)) {
                loggedInUser = register(sc, adminService);
                FileManager.saveUsers(adminService.getUsers());
                System.out.println("Registration successful. Logged in as " + loggedInUser.getRole());
            } else if ("3".equals(choice)) {
                System.out.println("Exiting...");
                System.exit(0);
            } else {
                System.out.println("Invalid choice");
            }
        }

        System.out.println("\nWelcome, " + loggedInUser.getName() + " (" + loggedInUser.getRole() + ")");

        // Role-based menu
        switch (loggedInUser.getRole()) {
            case "admin":
                adminMenu(adminService, studentService, sc, loggedInUser);
                break;
            case "student":
                studentMenu(studentService, adminService, sc, loggedInUser.getId());
                break;
            case "lecturer":
                lecturerMenu(sc);
                break;
            default:
                System.out.println("Unknown role. Exiting...");
        }

        // Save data
        FileManager.saveUsers(adminService.getUsers());
        // Handle saving safely
        try { FileManager.saveSubjects(adminService.getSubjects()); } catch(Exception e){}
        try { FileManager.saveResults(adminService.getResults()); } catch(Exception e){}

        sc.close();
        System.out.println("Exiting...");
    }

    // ------------------- Login -------------------
    private static User login(Scanner sc, UserService userService) {
        System.out.println("\n===== Login =====");
        System.out.print("Enter email: ");
        String email = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        return userService.login(email, password);
    }

    // ------------------- Register -------------------
    private static User register(Scanner sc, AdminService adminService) {
        System.out.println("\n===== Register =====");
        try {
            System.out.print("Enter ID: "); int id = Integer.parseInt(sc.nextLine());
            System.out.print("Enter Name: "); String name = sc.nextLine();
            System.out.print("Enter Email: "); String email = sc.nextLine();
            System.out.print("Enter Password: "); String pass = sc.nextLine();
            System.out.print("Enter Role (admin/student/lecturer): "); String role = sc.nextLine();

            User newUser = new User(id, name, email, pass, role.toLowerCase());
            adminService.addUser(newUser);
            return newUser;
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Creating dummy user.");
            return new User(0, "ErrorUser", "error", "error", "student");
        }
    }

    // ------------------- Admin Menu -------------------
    private static void adminMenu(AdminService adminService, StudentService studentService, Scanner sc, User user) {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n===== Admin Menu =====");
            System.out.println("1. User Management");
            System.out.println("2. Subject Management");
            System.out.println("3. Result Management");
            System.out.println("4. Student Management");
            System.out.println("5. Update Admin Info");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1": userMenu(adminService, sc); break;
                case "2": subjectMenu(adminService, sc); break;
                case "3": resultMenu(adminService, sc); break;
                case "4": studentMenu(studentService, adminService, sc); break;
                case "5": updateAdminInfo(user, sc); break;
                case "6": exit = true; break;
                default: System.out.println("Invalid choice");
            }
        }
    }

    // ------------------- User Menu -------------------
    private static void userMenu(AdminService adminService, Scanner sc) {
        System.out.println("\n--- User Menu ---");
        System.out.println("1. Add User");
        System.out.println("2. Update User");
        System.out.println("3. Delete User");
        System.out.println("4. List Users");
        System.out.print("Choice: ");
        String c = sc.nextLine();

        switch (c) {
            case "1":
                try {
                    System.out.print("Enter ID: "); int id = Integer.parseInt(sc.nextLine());
                    System.out.print("Enter Name: "); String name = sc.nextLine();
                    System.out.print("Enter Email: "); String email = sc.nextLine();
                    System.out.print("Enter Password: "); String pass = sc.nextLine();
                    System.out.print("Enter Role (student/lecturer/admin): "); String role = sc.nextLine();
                    adminService.addUser(new User(id, name, email, pass, role));
                    System.out.println("User added successfully.");
                } catch(Exception e) { System.out.println("Invalid input."); }
                break;
            case "2":
                System.out.print("Enter ID to update: "); int uid = Integer.parseInt(sc.nextLine());
                System.out.print("New Name (leave blank to skip): "); String n = sc.nextLine();
                System.out.print("New Email (leave blank to skip): "); String e = sc.nextLine();
                System.out.print("New Password (leave blank to skip): "); String p = sc.nextLine();
                adminService.updateUser(uid, n.isEmpty()?null:n, e.isEmpty()?null:e, p.isEmpty()?null:p);
                break;
            case "3":
                System.out.print("Enter ID to delete: "); int did = Integer.parseInt(sc.nextLine());
                adminService.deleteUser(did);
                break;
            case "4":
                adminService.listUsers();
                break;
            default:
                System.out.println("Invalid choice");
        }
    }

    // ------------------- Subject Menu -------------------
    private static void subjectMenu(AdminService adminService, Scanner sc) {
        System.out.println("\n--- Subject Menu ---");
        System.out.println("1. Add Subject");
        System.out.println("2. Update Subject");
        System.out.println("3. Assign Student");
        System.out.println("4. Assign Lecturer");
        System.out.println("5. List Subjects");
        System.out.print("Choice: ");
        String c = sc.nextLine();

        switch (c) {
            case "1":
                System.out.print("Enter Subject ID: "); String id = sc.nextLine();
                System.out.print("Enter Subject Name: "); String name = sc.nextLine();
                adminService.addSubject(new Subject(id, name));
                break;
            case "2":
                System.out.print("Enter Subject ID to update: "); String sid = sc.nextLine();
                System.out.print("New Name (leave blank to skip): "); String sname = sc.nextLine();
                adminService.updateSubject(sid, sname.isEmpty()?null:sname, null);
                break;
            case "3":
                System.out.print("Enter Subject ID: "); String subId = sc.nextLine();
                System.out.print("Enter Student ID: "); String stuId = sc.nextLine();
                adminService.assignSubjectToStudent(subId, stuId);
                break;
            case "4":
                System.out.print("Enter Subject ID: "); String sublId = sc.nextLine();
                System.out.print("Enter Lecturer Name: "); String lecName = sc.nextLine();
                adminService.assignSubjectToLecturer(sublId, new Lecturer(0, lecName, "", ""));
                break;
            case "5":
                adminService.listSubjects();
                break;
            default:
                System.out.println("Invalid choice");
        }
    }

    // ------------------- Result Menu -------------------
    private static void resultMenu(AdminService adminService, Scanner sc) {
        System.out.println("\n--- Result Menu ---");
        System.out.println("1. Approve All Results");
        System.out.println("2. List Results");
        System.out.print("Choice: ");
        String c = sc.nextLine();

        switch (c) {
            case "1":
                adminService.approveAllResults();
                break;
            case "2":
                adminService.listResults();
                break;
            default:
                System.out.println("Invalid choice");
        }
    }

    // ------------------- Student Menu -------------------
    private static void studentMenu(StudentService studentService, AdminService adminService, Scanner sc) {
        studentMenu(studentService, adminService, sc, 0); // Admin mode
    }

    private static void studentMenu(StudentService studentService, AdminService adminService, Scanner sc, int studentId) {
        if (studentId == 0) {
            // Admin mode
            System.out.println("\n--- Student Management ---");
            System.out.println("1. List Students");
            System.out.println("2. Add Student");
            System.out.print("Choice: ");
            String c = sc.nextLine();

            switch (c) {
                case "1":
                    studentService.displayStudents();
                    break;
                case "2":
                    try {
                        System.out.print("Enter ID: "); int nid = Integer.parseInt(sc.nextLine());
                        System.out.print("Enter Name: "); String nname = sc.nextLine();
                        System.out.print("Enter Email: "); String nemail = sc.nextLine();
                        System.out.print("Enter Password: "); String npass = sc.nextLine();
                        java.util.ArrayList<Student> cur = studentService.loadStudents();
                        cur.add(new Student(nid, nname, nemail, npass));
                        FileManager.saveStudents(cur);
                        System.out.println("Student added with ID: " + nid);
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid ID. Student not added.");
                    }
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        } else {
            // Student mode
            boolean exit = false;
            while (!exit) {
                System.out.println("\n===== Student Menu =====");
                System.out.println("1. View Profile");
                System.out.println("2. Take Exam");
                System.out.println("3. View Results");
                System.out.println("4. Exit");
                System.out.print("Choice: ");
                String c = sc.nextLine();

                switch (c) {
                    case "1":
                        Student student = studentService.getStudentById(studentId);
                        if (student != null) {
                            System.out.println("Profile: " + student);
                        } else {
                            System.out.println("Student not found.");
                        }
                        break;
                    case "2":
                        System.out.print("Enter Exam ID: ");
                        String examId = sc.nextLine();
                        studentService.takeExam(studentId, examId);
                        break;
                    case "3":
                        System.out.println("\n--- Your Results ---");
                        adminService.loadResultsFromFile();
                        var results = adminService.getResults();
                        var studentResults = new java.util.ArrayList<>();
                        for (Object res : results) {
                            if (res.toString().contains("studentId='" + studentId) || res.toString().startsWith(studentId + ",")) {
                                studentResults.add(res);
                            }
                        }
                        if (studentResults.isEmpty()) {
                            System.out.println("No results found for your ID.");
                        } else {
                            for (Object res : studentResults) {
                                System.out.println(res);
                            }
                        }
                        break;
                    case "4": exit = true; break;
                    default: System.out.println("Invalid choice");
                }
            }
        }
    }

    // -----------------------------------------------------------------
    // ------------------- LECTURER MENU (UPDATED) ---------------------
    // -----------------------------------------------------------------
    private static void lecturerMenu(Scanner sc) {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n===== Lecturer Dashboard =====");
            System.out.println("1. Create Exam");
            System.out.println("2. Modify Exam");
            System.out.println("3. Delete Exam");
            System.out.println("4. View Exams");
            System.out.println("5. Student Reports");
            System.out.println("6. Exit");
            System.out.print("Choice: ");
            String c = sc.nextLine();

            switch (c) {
                case "1":
                    createExamConsole(sc);
                    break;
                case "2":
                    modifyExamConsole(sc);
                    break;
                case "3":
                    deleteExamConsole(sc);
                    break;
                case "4":
                    viewExamsConsole();
                    break;
                case "5":
                    viewStudentReportsConsole();
                    break;
                case "6": 
                    exit = true; 
                    break;
                default: System.out.println("Invalid choice");
            }
        }
    }

    // --- Helper Methods for Lecturer (Direct File Handling) ---

    private static void createExamConsole(Scanner sc) {
        System.out.println("\n--- Create New Exam ---");
        System.out.print("Enter Exam ID: "); String id = sc.nextLine();
        System.out.print("Enter Title: "); String title = sc.nextLine();
        System.out.print("Enter Subject: "); String subject = sc.nextLine();
        System.out.print("Enter Duration (mins): "); String duration = sc.nextLine();

        try (FileWriter fw = new FileWriter("data/exams.txt", true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(id + "," + title + "," + subject + "," + duration);
            System.out.println(">> Exam created successfully!");
        } catch (IOException e) {
            System.out.println("Error saving exam: " + e.getMessage());
        }
    }

    private static void modifyExamConsole(Scanner sc) {
        System.out.println("\n--- Modify Exam ---");
        System.out.print("Enter Exam ID to modify: "); String targetId = sc.nextLine();
        
        File inputFile = new File("data/exams.txt");
        File tempFile = new File("data/exams_temp.txt");
        boolean found = false;

        // Ensure file exists
        if(!inputFile.exists()) {
            System.out.println("No exams file found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(targetId)) {
                    found = true;
                    System.out.println("Found Exam: " + (parts.length > 1 ? parts[1] : "N/A"));
                    System.out.print("New Title (Enter to keep current): "); 
                    String t = sc.nextLine();
                    String newTitle = t.isEmpty() && parts.length > 1 ? parts[1] : t;

                    System.out.print("New Subject (Enter to keep current): "); 
                    String s = sc.nextLine();
                    String newSub = s.isEmpty() && parts.length > 2 ? parts[2] : s;

                    System.out.print("New Duration (Enter to keep current): "); 
                    String d = sc.nextLine();
                    String newDur = d.isEmpty() && parts.length > 3 ? parts[3] : d;

                    writer.println(targetId + "," + newTitle + "," + newSub + "," + newDur);
                } else {
                    writer.println(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        if (found) {
            inputFile.delete();
            tempFile.renameTo(inputFile);
            System.out.println(">> Exam updated successfully!");
        } else {
            tempFile.delete();
            System.out.println(">> Exam ID not found.");
        }
    }

    private static void deleteExamConsole(Scanner sc) {
        System.out.println("\n--- Delete Exam ---");
        System.out.print("Enter Exam ID to delete: "); String targetId = sc.nextLine();

        File inputFile = new File("data/exams.txt");
        File tempFile = new File("data/exams_temp.txt");
        boolean found = false;

        if(!inputFile.exists()) {
            System.out.println("No exams file found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(targetId)) {
                    found = true;
                    continue; // Skip writing this line
                }
                writer.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        if (found) {
            inputFile.delete();
            tempFile.renameTo(inputFile);
            System.out.println(">> Exam deleted successfully!");
        } else {
            tempFile.delete();
            System.out.println(">> Exam ID not found.");
        }
    }

    private static void viewExamsConsole() {
        System.out.println("\n--- Available Exams ---");
        File file = new File("data/exams.txt");
        if (!file.exists()) {
            System.out.println("No exams found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            System.out.printf("%-10s %-20s %-15s %-10s\n", "ID", "Title", "Subject", "Time");
            System.out.println("---------------------------------------------------------");
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    System.out.printf("%-10s %-20s %-15s %-10s\n", parts[0], parts[1], parts[2], parts[3]);
                } else {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }

    private static void viewStudentReportsConsole() {
        System.out.println("\n--- Student Reports ---");
        File file = new File("data/results.txt");
        if (!file.exists()) {
            System.out.println("No results found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            // Assuming format: StudentID,ExamID,Score
            System.out.printf("%-15s %-15s %-10s\n", "Student ID", "Exam ID", "Score");
            System.out.println("-----------------------------------------");
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    System.out.printf("%-15s %-15s %-10s\n", parts[0], parts[1], parts[2]);
                } else {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading results.");
        }
    }

    // ------------------- Update Admin Info -------------------
    private static void updateAdminInfo(User user, Scanner sc) {
        System.out.print("New Name (leave blank to skip): "); String n = sc.nextLine();
        System.out.print("New Email (leave blank to skip): "); String e = sc.nextLine();
        System.out.print("New Password (leave blank to skip): "); String p = sc.nextLine();
        if (!n.isEmpty()) user.setName(n);
        if (!e.isEmpty()) user.setEmail(e);
        if (!p.isEmpty()) user.setPassword(p);
        System.out.println("Admin info updated!");
    }
}