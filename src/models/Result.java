package models;

public class Result extends User {
    private String course;
    private double marks;

    public Result(int id, String name, String email, String password, String role,
                  String course, double marks) {
        super(id, name, email, password, role);
        this.course = course;
        this.marks = marks;
    }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public double getMarks() { return marks; }
    public void setMarks(double marks) { this.marks = marks; }

    public String calculateGrade() {
        if (marks >= 90) return "A";
        else if (marks >= 80) return "B";
        else if (marks >= 70) return "C";
        else if (marks >= 60) return "D";
        else return "F";
    }

    public void displayResult() {
        System.out.println("Student ID: " + id);  
        System.out.println("Name: " + name);     
        System.out.println("Email: " + email);   
        System.out.println("Role: " + role);     
        System.out.println("Course: " + course);
        System.out.println("Marks: " + marks);
        System.out.println("Grade: " + calculateGrade());
    }
}
