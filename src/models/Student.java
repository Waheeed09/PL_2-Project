package models;

public class Student extends User {

    public Student(int id, String name, String email, String password) {
        super(id, name, email, password, "student");
    }

    public void answerQuestion(String questionId, String answerText) {
        for (String[] a : answers) {
            if (a[0].equals(questionId)) {
                a[1] = answerText;
                return;
            }
        }
        answers.add(new String[]{questionId, answerText});
    }

    public String getAnswer(String questionId) {
        for (String[] a : answers) {
            if (a[0].equals(questionId)) {
                return a[1];
            }
        }
        return null;
    }
}
