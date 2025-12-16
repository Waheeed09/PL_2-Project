package models;

public class Recorrection {
    private String studentId;
    private String resultId;
    private String reason;

    public Recorrection(String studentId, String resultId, String reason) {
        this.studentId = studentId;
        this.resultId = resultId;
        this.reason = reason;
    }

    public String getStudentId() { return studentId; }
    public String getResultId() { return resultId; }
    public String getReason() { return reason; }

    @Override
    public String toString() {
        return "Recorrection{" + "studentId='" + studentId + '\'' + ", resultId='" + resultId + '\'' + ", reason='" + reason + '\'' + '}';
    }
}
