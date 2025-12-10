package com.mycompany.mavenproject1;
import java.io.*;
import java.util.*;
import com.google.gson.*;
public class Admin {
class User {
    String id;
    String username;
    String password;
    String role;
    public User(String id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}

class Student extends User {
    String name;
    List<String> enrolledSubjects;
    Map<String, Double> grades;
    
    public Student(String id, String username, String password, String name) {
        super(id, username, password, "STUDENT");
        this.name = name;
        this.enrolledSubjects = new ArrayList<>();
        this.grades = new HashMap<>();
    }
}

class Lecturer extends User {
    String name;
    List<String> assignedSubjects;
    
    public Lecturer(String id, String username, String password, String name) {
        super(id, username, password, "LECTURER");
        this.name = name;
        this.assignedSubjects = new ArrayList<>();
    }
}

class Admin extends User {
    public Admin(String id, String username, String password) {
        super(id, username, password, "ADMIN");
    }
}

class Subject {
    String subjectId;
    String subjectName;
    String lecturerId;
    List<String> enrolledStudents;
    
    public Subject(String subjectId, String subjectName, String lecturerId) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.lecturerId = lecturerId;
        this.enrolledStudents = new ArrayList<>();
    }
}

class Grade {
    String studentId;
    String subjectId;
    double score;
    boolean approved;
    String approvedBy;
    
    public Grade(String studentId, String subjectId, double score) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.score = score;
        this.approved = false;
    }
}

class DataStore {
    private static final String STUDENTS_FILE = "students.json";
    private static final String LECTURERS_FILE = "lecturers.json";
    private static final String ADMINS_FILE = "admins.json";
    private static final String SUBJECTS_FILE = "subjects.json";
    private static final String GRADES_FILE = "grades.json";
    
    private Gson gson;
    
    public DataStore() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }
    
    public List<Student> loadStudents() {
        return loadFromFile(STUDENTS_FILE, Student[].class);
    }
    
    public void saveStudents(List<Student> students) {
        saveToFile(STUDENTS_FILE, students);
    }
    
    public List<Lecturer> loadLecturers() {
        return loadFromFile(LECTURERS_FILE, Lecturer[].class);
    }
    
    public void saveLecturers(List<Lecturer> lecturers) {
        saveToFile(LECTURERS_FILE, lecturers);
    }
    
    public List<Admin> loadAdmins() {
        return loadFromFile(ADMINS_FILE, Admin[].class);
    }
    
    public void saveAdmins(List<Admin> admins) {
        saveToFile(ADMINS_FILE, admins);
    }
    
    public List<Subject> loadSubjects() {
        return loadFromFile(SUBJECTS_FILE, Subject[].class);
    }
    
    public void saveSubjects(List<Subject> subjects) {
        saveToFile(SUBJECTS_FILE, subjects);
    }
    
    public List<Grade> loadGrades() {
        return loadFromFile(GRADES_FILE, Grade[].class);
    }
    
    public void saveGrades(List<Grade> grades) {
        saveToFile(GRADES_FILE, grades);
    }
    
    private <T> List<T> loadFromFile(String filename, Class<T[]> classType) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            FileReader reader = new FileReader(file);
            T[] array = gson.fromJson(reader, classType);
            reader.close();
            return array == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(array));
        } catch (Exception e) {
            System.out.println("Error loading " + filename + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private <T> void saveToFile(String filename, List<T> data) {
        try {
            FileWriter writer = new FileWriter(filename);
            gson.toJson(data, writer);
            writer.close();
        } catch (Exception e) {
            System.out.println("Error saving " + filename + ": " + e.getMessage());
        }
    }
}

class AdministrativeModule {
    private DataStore dataStore;
    private List<Student> students;
    private List<Lecturer> lecturers;
    private List<Admin> admins;
    private List<Subject> subjects;
    private List<Grade> grades;
    private Admin currentAdmin;
    
    public AdministrativeModule() {
        this.dataStore = new DataStore();
        loadAllData();
    }
    
    private void loadAllData() {
        this.students = dataStore.loadStudents();
        this.lecturers = dataStore.loadLecturers();
        this.admins = dataStore.loadAdmins();
        this.subjects = dataStore.loadSubjects();
        this.grades = dataStore.loadGrades();
    }
    
    private void saveAllData() {
        dataStore.saveStudents(students);
        dataStore.saveLecturers(lecturers);
        dataStore.saveAdmins(admins);
        dataStore.saveSubjects(subjects);
        dataStore.saveGrades(grades);
    }
    
    public boolean authenticateAdmin(String username, String password) {
        for (Admin admin : admins) {
            if (admin.username.equals(username) && admin.password.equals(password)) {
                this.currentAdmin = admin;
                return true;
            }
        }
        return false;
    }
    
    public boolean updateAdminCredentials(String newUsername, String newPassword) {
        if (currentAdmin == null) return false;
        currentAdmin.username = newUsername;
        currentAdmin.password = newPassword;
        dataStore.saveAdmins(admins);
        return true;
    }
    
    public boolean addStudent(String id, String username, String password, String name) {
        for (Student s : students) {
            if (s.id.equals(id)) return false;
        }
        students.add(new Student(id, username, password, name));
        dataStore.saveStudents(students);
        return true;
    }
    
    public boolean deleteStudent(String id) {
        boolean removed = students.removeIf(s -> s.id.equals(id));
        if (removed) {
            for (Subject subject : subjects) {
                subject.enrolledStudents.remove(id);
            }
            grades.removeIf(g -> g.studentId.equals(id));
            saveAllData();
        }
        return removed;
    }
    
    public boolean updateStudent(String id, String newName, String newUsername) {
        for (Student s : students) {
            if (s.id.equals(id)) {
                s.name = newName;
                s.username = newUsername;
                dataStore.saveStudents(students);
                return true;
            }
        }
        return false;
    }
    
    public List<Student> listAllStudents() {
        return new ArrayList<>(students);
    }
    
    public Student searchStudent(String id) {
        for (Student s : students) {
            if (s.id.equals(id)) return s;
        }
        return null;
    }
    
    public boolean addLecturer(String id, String username, String password, String name) {
        for (Lecturer l : lecturers) {
            if (l.id.equals(id)) return false;
        }
        lecturers.add(new Lecturer(id, username, password, name));
        dataStore.saveLecturers(lecturers);
        return true;
    }
    
    public boolean deleteLecturer(String id) {
        boolean removed = lecturers.removeIf(l -> l.id.equals(id));
        if (removed) {
            for (Subject subject : subjects) {
                if (subject.lecturerId.equals(id)) {
                    subject.lecturerId = null;
                }
            }
            saveAllData();
        }
        return removed;
    }
    
    public boolean updateLecturer(String id, String newName, String newUsername) {
        for (Lecturer l : lecturers) {
            if (l.id.equals(id)) {
                l.name = newName;
                l.username = newUsername;
                dataStore.saveLecturers(lecturers);
                return true;
            }
        }
        return false;
    }
    
    public List<Lecturer> listAllLecturers() {
        return new ArrayList<>(lecturers);
    }
    
    public Lecturer searchLecturer(String id) {
        for (Lecturer l : lecturers) {
            if (l.id.equals(id)) return l;
        }
        return null;
    }
    
    public boolean addSubject(String subjectId, String subjectName) {
        for (Subject sub : subjects) {
            if (sub.subjectId.equals(subjectId)) return false;
        }
        subjects.add(new Subject(subjectId, subjectName, null));
        dataStore.saveSubjects(subjects);
        return true;
    }
    
    public boolean assignSubjectToLecturer(String subjectId, String lecturerId) {
        Subject subject = null;
        for (Subject sub : subjects) {
            if (sub.subjectId.equals(subjectId)) {
                subject = sub;
                break;
            }
        }
        
        Lecturer lecturer = null;
        for (Lecturer l : lecturers) {
            if (l.id.equals(lecturerId)) {
                lecturer = l;
                break;
            }
        }
        
        if (subject == null || lecturer == null) return false;
        
        subject.lecturerId = lecturerId;
        if (!lecturer.assignedSubjects.contains(subjectId)) {
            lecturer.assignedSubjects.add(subjectId);
        }
        
        saveAllData();
        return true;
    }
    
    public boolean enrollStudentInSubject(String studentId, String subjectId) {
        Student student = null;
        for (Student s : students) {
            if (s.id.equals(studentId)) {
                student = s;
                break;
            }
        }
        
        Subject subject = null;
        for (Subject sub : subjects) {
            if (sub.subjectId.equals(subjectId)) {
                subject = sub;
                break;
            }
        }
        
        if (student == null || subject == null) return false;
        
        if (!subject.enrolledStudents.contains(studentId)) {
            subject.enrolledStudents.add(studentId);
        }
        if (!student.enrolledSubjects.contains(subjectId)) {
            student.enrolledSubjects.add(subjectId);
        }
        
        saveAllData();
        return true;
    }
    
    public List<Grade> getPendingGrades() {
        List<Grade> pending = new ArrayList<>();
        for (Grade g : grades) {
            if (!g.approved) {
                pending.add(g);
            }
        }
        return pending;
    }
    
    public boolean approveGrade(String studentId, String subjectId) {
        if (currentAdmin == null) return false;
        
        for (Grade g : grades) {
            if (g.studentId.equals(studentId) && g.subjectId.equals(subjectId) && !g.approved) {
                g.approved = true;
                g.approvedBy = currentAdmin.id;
                
                for (Student s : students) {
                    if (s.id.equals(studentId)) {
                        s.grades.put(subjectId, g.score);
                        break;
                    }
                }
                
                saveAllData();
                return true;
            }
        }
        return false;
    }
    
    public boolean publishGrades(String subjectId) {
        if (currentAdmin == null) return false;
        
        boolean published = false;
        for (Grade g : grades) {
            if (g.subjectId.equals(subjectId) && !g.approved) {
                g.approved = true;
                g.approvedBy = currentAdmin.id;
                
                for (Student s : students) {
                    if (s.id.equals(studentId)) {
                        s.grades.put(subjectId, g.score);
                    }
                }
                published = true;
            }
        }
        if (published) {
            saveAllData();
        }
        return published;
    }
}
public class ExamManagementSystem {
    public static void main(String[] args) {
       Admin na = new Admin();
        Scanner input = new Scanner(System.in);
        
        System.out.print("Enter admin username: ");
        String username = input.nextLine();
        System.out.print("Enter admin password: ");
        String password = input.nextLine();
        
        if (Admin.authenticateAdmin(username, password)) {
            System.out.println("Login successful!");
            
            adminModule.addStudent("S001", "john_doe", "pass123", "John Doe");
            adminModule.addLecturer("L001", "prof_smith", "prof123", "Dr. Smith");
            adminModule.addSubject("CS101", "Introduction to Computer Science");
            
            adminModule.assignSubjectToLecturer("CS101", "L001");
            adminModule.enrollStudentInSubject("S001", "CS101");
            
            System.out.println("System initialized with sample data.");
        } else {
            System.out.println("Authentication failed!");
        }
        
        input.close();
    }
  }
}
