import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import models.Admin;
import models.Exam;
import models.Lecturer;
import models.Question;
import models.Student;
import models.Subject;
import models.User;
import services.AdminService;
import services.FileManager;
import services.LecturerService;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AdminService adminService = new AdminService();

        // Load data from files
        adminService.setUsers(FileManager.loadUsers());
        adminService.setSubjects(FileManager.loadSubjects());
        adminService.setResults(FileManager.loadResults());

        Admin admin = new Admin(1, "Admin", "admin@example.com", "1234");

        boolean exit = false;

        while (!exit) {
            System.out.println("\n===== Admin Menu =====");
            System.out.println("1. User Management");
            System.out.println("2. Subject Management");
            System.out.println("3. Result Management");
            System.out.println("4. Update Admin Info");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1": userMenu(adminService, sc); break;
                case "2": subjectMenu(adminService, sc); break;
                case "3": resultMenu(adminService, sc); break;
                case "4": updateAdminInfo(admin, sc); break;
                case "5": exit = true; break;
                default: System.out.println("Invalid choice");
            }

            // Save after every operation
            FileManager.saveUsers(adminService.getUsers());
            FileManager.saveSubjects(adminService.getSubjects());
            FileManager.saveResults(adminService.getResults());
        }

        // Lecturer Demo
        lecturerDemo();

        sc.close();
        System.out.println("Exiting Admin Console...");
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

    // ------------------- Lecturer Demo -------------------
    private static void lecturerDemo() {
        System.out.println("\n===== Lecturer Demo =====");

        Lecturer lecturer = new Lecturer(1, "Dr. Ahmed", "ahmed@example.com", "1234");
        LecturerService lecturerService = new LecturerService();

        String examId = "EX001";
        String examTitle = "Java Basics";

        String subjectId = "SUBJ001";
        String lecturerId = String.valueOf(lecturer.getId());
        int durationMinutes = 60;

        Exam exam = new Exam(examId, examTitle, subjectId, lecturerId, durationMinutes);

        // إضافة أسئلة
        Question q1 = new Question(1, examId, "What is JVM?", "Java Virtual Machine", "SA", null, 1);
        Question q2 = new Question(2, examId, "Java is platform independent? (True/False)", "True", "TF", null, 1);
        Question q3 = new Question(3, examId, "Which keyword is used for inheritance?", "extends", "MC", Arrays.asList("super", "extends", "implements", "this"), 1);

        lecturerService.addQuestion(lecturer, exam, q1);
        lecturerService.addQuestion(lecturer, exam, q2);
        lecturerService.addQuestion(lecturer, exam, q3);

        // إنشاء طالب
        Student student = new Student(101, "Ali", "ali@example.com", "pass");

        // الطالب يجاوب
        student.answerQuestion(1, "Java Virtual Machine");
        student.answerQuestion(2, "True");
        student.answerQuestion(3, "extends");

        // حساب الدرجة
        int score = lecturerService.calculateGrade(exam, student);
        System.out.println("Student Score: " + score);

        // تقرير الصف
        List<Student> students = new ArrayList<>();
        students.add(student);
        lecturerService.generateClassReport(exam, students);
    }
}
