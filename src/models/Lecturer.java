package models;

public class Lecturer extends User {

    public Lecturer() {
        super();
        this.role = "lecturer";
    }

    public Lecturer(int id, String name, String email, String password) {
        super(id, name, email, password, "lecturer");
    }
}