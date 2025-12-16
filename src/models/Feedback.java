package models;

public class Feedback {
    private String studentId;
    private String text;

    public Feedback(String studentId, String text) {
        this.studentId = studentId;
        this.text = text;
    }

    public String getStudentId() { return studentId; }
    public String getText() { return text; }

    @Override
    public String toString() {
        return "Feedback{" + "studentId='" + studentId + '\'' + ", text='" + text + '\'' + '}';
    }
}
