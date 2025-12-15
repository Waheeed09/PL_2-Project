package models;

import java.util.ArrayList;
import java.util.List;

public class Subject {

    
     private String subjectId;
    private String subjectName;
    private Lecturer lecturer; // ممكن يكون null
    private List<String> enrolledStudents;

    public Subject(String subjectId, String subjectName) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.enrolledStudents = new ArrayList<>();
        this.lecturer = null; // افتراضي
    }


    // Getters & Setters
    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Lecturer getLecturer() {
        return lecturer;
    }

    public void setLecturer(Lecturer lecturer) {
        this.lecturer = lecturer;
    }

    // Get lecturer ID easily
    public int getLecturerId() {
        return lecturer.getId();   // ID inherited from User
    }

    public List<String> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<String> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    // Utility Methods Feha Methods tba3 el admin bs eshta tt7t hna msh 7war
    public void addSubjectToStudent(String studentId) {
        if (!enrolledStudents.contains(studentId)) {
            enrolledStudents.add(studentId);
        }
    }
    public void removeStudent(int studentId) {
        enrolledStudents.remove(studentId);
    }

    public int getEnrollmentCount() {
        return enrolledStudents.size();
    }
}
