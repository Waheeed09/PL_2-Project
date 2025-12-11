package models;

public class Student extends User {

    private String level;

    public Student(int id, String name, String email, String password, String level) {
        super(id, name, email, password);
        this.level = level;
    }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
}
