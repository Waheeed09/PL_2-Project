import java.util.Scanner;
import models.Admin;
import models.Lecturer;
import models.Student;
import models.Subject;
import models.User;
import services.AdminService;
import services.FileManager;
import services.LecturerService;
import services.StudentService;
import services.UserService;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AdminService adminService = new AdminService();
        StudentService studentService = new StudentService();
        UserService userService;

        // Load data from files
        adminService.setUsers(FileManager.loadUsers());
        adminService.setSubjects(FileManager.loadSubjects());
        adminService.setResults(FileManager.loadResults());
        studentService.loadStudents();

        userService = new UserService(adminService.getUsers());

        // Login
        User loggedInUser = login(sc, userService);
        if (loggedInUser == null) {
            System.out.println("Login failed. Exiting...");
            sc.close();
            return;
        }

        // Role-based menu
        if ("admin".equals(loggedInUser.getRole())) {
            adminMenu(adminService, studentService, sc);
        } else if ("student".equals(loggedInUser.getRole())) {
            studentMenu(studentService, sc, loggedInUser.getId());
        } else if ("lecturer".equals(loggedInUser.getRole())) {
            lecturerMenu(sc);
        } else {
            System.out.println("Unknown role. Exiting...");
        }

        // Save data
        FileManager.saveUsers(adminService.getUsers());
        FileManager.saveSubjects(adminService.getSubjects());
        FileManager.saveResults(adminService.getResults());

        sc.close();
        System.out.println("Exiting...");
    }

    // ------------------- Login -------------------
    private static User login(Scanner sc, UserService userService) {
        System.out.println("===== Login =====");
        System.out.print("Enter email: ");
        String email = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        User user = userService.login(email, password);
        FileManager.saveLoginLog(email, user != null);
        return user;
    }

    // ------------------- Admin Menu -------------------
    private static void adminMenu(AdminService adminService, StudentService studentService, Scanner sc) {
        Admin admin = new Admin(1, "Admin", "admin@example.com", "1234");

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
                case "4": studentMenu(studentService, sc); break;
                case "5": updateAdminInfo(admin, sc); break;
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
                System.out.print("Enter ID: "); int id = Integer.parseInt(sc.nextLine());
                System.out.print("Enter Name: "); String name = sc.nextLine();
                System.out.print("Enter Email: "); String email = sc.nextLine();
                System.out.print("Enter Password: "); String pass = sc.nextLine();
                System.out.print("Enter Role (student/lecturer/admin): "); String role = sc.nextLine();
                adminService.addUser(new User(id, name, email, pass, role));
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
                adminService.getResults();
                break;
            default:
                System.out.println("Invalid choice");
        }
    }

    // ------------------- Student Menu -------------------
    private static void studentMenu(StudentService studentService, Scanner sc) {
        studentMenu(studentService, sc, 0); // Admin mode
    }

    private static void studentMenu(StudentService studentService, Scanner sc, int studentId) {
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
                    // Add student logic
                    System.out.println("Add student not implemented");
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
                        // View results for student
                        System.out.println("Results: (Not implemented yet)");
                        break;
                    case "4": exit = true; break;
                    default: System.out.println("Invalid choice");
                }
            }
        }
    }

    // ------------------- Lecturer Menu -------------------
    private static void lecturerMenu(Scanner sc) {
        Lecturer lecturer = new Lecturer(1, "Dr. Ahmed", "ahmed@example.com", "1234");
        LecturerService lecturerService = new LecturerService();

        boolean exit = false;
        while (!exit) {
            System.out.println("\n===== Lecturer Menu =====");
            System.out.println("1. Create Exam");
            System.out.println("2. Modify Exam");
            System.out.println("3. Delete Exam");
            System.out.println("4. View Exams");
            System.out.println("5. Exit");
            System.out.print("Choice: ");
            String c = sc.nextLine();

            switch (c) {
                case "1":
                    System.out.print("Enter Exam ID: ");
                    String examId = sc.nextLine();
                    System.out.print("Enter Exam Title: ");
                    String title = sc.nextLine();
                    lecturerService.createExam(lecturer, examId, title);
                    break;
                case "2":
                    System.out.print("Enter Old Exam ID: ");
                    String oldId = sc.nextLine();
                    System.out.print("Enter New Exam ID: ");
                    String newId = sc.nextLine();
                    System.out.print("Enter New Title: ");
                    String newTitle = sc.nextLine();
                    lecturerService.modifyExam(lecturer, oldId, newId, newTitle);
                    break;
                case "3":
                    System.out.print("Enter Exam ID to delete: ");
                    String delId = sc.nextLine();
                    lecturerService.deleteExam(lecturer, delId);
                    break;
                case "4":
                    lecturerService.viewExams(lecturer);
                    break;
                case "5": exit = true; break;
                default: System.out.println("Invalid choice");
            }
        }
    }

    // ------------------- Update Admin Info -------------------
    private static void updateAdminInfo(Admin admin, Scanner sc) {
        System.out.print("New Name (leave blank to skip): "); String n = sc.nextLine();
        System.out.print("New Email (leave blank to skip): "); String e = sc.nextLine();
        System.out.print("New Password (leave blank to skip): "); String p = sc.nextLine();
        if (!n.isEmpty()) admin.setName(n);
        if (!e.isEmpty()) admin.setEmail(e);
        if (!p.isEmpty()) admin.setPassword(p);
        System.out.println("Admin info updated!");
    }
}
