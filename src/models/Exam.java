package models;

public class Exam {
    public String examId;
    public String subjectName;
    public int duration;

    public Exam(String id, String sub, int d) {
        examId = id;
        subjectName = sub;
        duration = d;
    }
}
