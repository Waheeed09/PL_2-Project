package models;

public class Result {
    private String studentId;
    private String subjectId;
    private double grade;
    private boolean approved;

    public Result(String studentId, String subjectId, double grade) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.grade = grade;
        this.approved = false; // default
    }

    // Getters and setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }

    public double getGrade() { return grade; }
    public void setGrade(double grade) { this.grade = grade; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    @Override
    public String toString() {
        return "Result{" +
               "studentId='" + studentId + '\'' +
               ", subjectId='" + subjectId + '\'' +
               ", grade=" + grade +
               ", approved=" + approved +
               '}';
    }
}

