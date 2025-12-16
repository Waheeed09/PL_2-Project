package models;

public class Submission {
    private String studentId;
    private String examId;
    private String answer;

    public Submission(String studentId, String examId, String answer) {
        this.studentId = studentId;
        this.examId = examId;
        this.answer = answer;
    }

    public String getStudentId() { return studentId; }
    public String getExamId() { return examId; }
    public String getAnswer() { return answer; }

    @Override
    public String toString() {
        return "Submission{" + "studentId='" + studentId + '\'' + ", examId='" + examId + '\'' + ", answer='" + answer + '\'' + '}';
    }
}
