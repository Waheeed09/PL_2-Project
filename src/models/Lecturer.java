package models;

public class Lecturer extends User {

    public Lecturer(int id, String name, String email, String password) {
        super(id, name, email, password, "lecturer");
    }
}