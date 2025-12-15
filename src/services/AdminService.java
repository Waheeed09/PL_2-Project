package services;

import java.util.ArrayList;
import java.util.List;
import models.Admin;
import models.Lecturer;
import models.Result;
import models.Subject;
import models.User;

public class AdminService {

    private List<User> users = new ArrayList<>();
    private List<Subject> subjects = new ArrayList<>();
    private List<Result> results = new ArrayList<>();
     // -------------------
    // Admin Self Management
    // -------------------
    public void updateAdminInfo(Admin admin, String name, String email, String password) {
        if (name != null) admin.setName(name);
        if (email != null) admin.setEmail(email);
        if (password != null) admin.setPassword(password);
        System.out.println("✔ Admin info updated: " + admin);
    }

    // -------------------
    // User Management
    // -------------------
   public void addUser(User user) {
    if (user == null) {
        System.out.println("Invalid user");
        return;
    }//Defensive Programming حمايه الكود من 

    for (User u : users) {
        if (u.getEmail().equals(user.getEmail())) {
            System.out.println("Email already exists");
            return;
        }
    }

    users.add(user);
    System.out.println("User added successfully");
}


    public boolean deleteUser(int id) {
        for (User u : users) {
            if (u.getId() == id) {
                users.remove(u);
                System.out.println(" User deleted: " + u);
                return true;
            }
        }
        System.out.println(" User not found!");
        return false;
    }

    public boolean updateUser(int id, String name, String email, String password) {
    if (users.isEmpty()) {
        System.out.println("No users found");
        return false;
    }

    for (User u : users) {
        if (u.getId() == id) {
            if (name != null) u.setName(name);
            if (email != null) u.setEmail(email);
            if (password != null) u.setPassword(password);

            System.out.println("User updated successfully");
            return true;
        }
    }

    System.out.println("User not found");
    return false;
}


    public User searchUser(int id) {
        for (User u : users) {
            if (u.getId() == id) return u;
        }
        return null;
    }

    public void listUsers() {
        System.out.println("===== All Users =====");
        for (User u : users) System.out.println(u);
        System.out.println("=====================");
    }

    // -------------------
    // Subject Management
    // -------------------
    public void addSubject(Subject subject) {
        if (subject == null) return;
        subjects.add(subject);
        System.out.println(" Subject added: " + subject.getSubjectName());
    }

    public boolean removeSubject(String subjectId) {
        for (Subject s : subjects) {
            if (s.getSubjectId().equals(subjectId)) {
                subjects.remove(s);
                System.out.println(" Subject removed: " + s.getSubjectName());
                return true;
            }
        }
        System.out.println(" Subject not found!");
        return false;
    }

    public boolean updateSubject(String subjectId, String name, Lecturer lecturer) {
        for (Subject s : subjects) {
            if (s.getSubjectId().equals(subjectId)) {
                if (name != null) s.setSubjectName(name);
                if (lecturer != null) s.setLecturer(lecturer);
                System.out.println(" Subject updated: " + s.getSubjectName());
                return true;
            }
        }
        System.out.println(" Subject not found!");
        return false;
    }

    public boolean assignSubjectToStudent(String subjectId, String studentId) {
          if (subjectId == null || studentId == null) {
        return false;
    }
        for (Subject s : subjects) {
            if (s.getSubjectId().equals(subjectId)) {
                s.addSubjectToStudent(studentId);
                System.out.println(" Student  " + studentId + " assigned to  " + s.getSubjectName());
                return true;
            }
        }
        System.out.println(" Subject not found!");
        return false;
    }

    public boolean assignSubjectToLecturer(String subjectId, Lecturer lecturer) {
         if (subjectId == null || lecturer == null) {
        return false;
    }
        for (Subject s : subjects) {
            if (s.getSubjectId().equals(subjectId)) {
                s.setLecturer(lecturer);
                System.out.println(" Lecturer  " + lecturer.getName() + " assigned to  " + s.getSubjectName());
                return true;
            }
        }
        System.out.println(" Subject not found!");
        return false;
    }
  
    public void listSubjects() {
        System.out.println("===== All Subjects =====");
        for (Subject s : subjects) {
            String lecName = s.getLecturer() != null ? s.getLecturer().getName() : "None";
            System.out.println("ID: " + s.getSubjectId() + ", Name: " + s.getSubjectName() + ", Lecturer: " + lecName + ", Students enrolled: " + s.getEnrolledStudents().size());
        }
        System.out.println("========================");
    }

   

    // -------------------
    //  Result Management
    // -------------------
    public List<Result> getResults() {
        if (results.isEmpty()) {
            System.out.println("No results available");
        } else {
            System.out.println("Listing all results:");
            for (Result r : results) {
                System.out.println(r);
            }
        }
        return results;
    }
    public void approveAllResults() {
        if (results.isEmpty()) {
            System.out.println("No results to approve");
            return;
        }

        for (Result r : results) {
            r.setApproved(true); 
        }
        System.out.println("All results approved successfully");
    }
    
}
