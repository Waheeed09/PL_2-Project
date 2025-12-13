package models;
import java.util.ArrayList;

public class Exam {
    public String examId;
    public String subjectName;
    public ArrayList<Question1> questions = new ArrayList<>();

    public Exam(String id, String sub) {
        examId = id;
        subjectName = sub;

    }
}