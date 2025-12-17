package models;

public class Feedback {
    private String studentId;
    private String examId;
    private String text;

    public Feedback(String studentId, String examId, String text) {
        this.studentId = studentId;
        this.examId = examId;
        this.text = text;
    }

    public String getStudentId() { return studentId; }
    public String getExamId() { return examId; }
    public String getText() { return text; }

    @Override
    public String toString() {
        return "Feedback{" + "studentId='" + studentId + '\'' + ", examId='" + examId + '\'' + ", text='" + text + '\'' + '}';
    }
}
