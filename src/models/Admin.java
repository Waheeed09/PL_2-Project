package models;

public class Admin extends User {

    private String username;

    public Admin(int id, String name, String email, String password, String username) {
        super(id, name, email, password);
        this.username = username;
    }

}
