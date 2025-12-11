package models;

public class Lecturer extends User {

    private String subject;

    public Lecturer(int id, String name, String email, String password, String subject) {
        super(id, name, email, password);
        this.subject = subject;
    }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
}