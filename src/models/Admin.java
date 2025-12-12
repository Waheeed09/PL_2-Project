package models;
public class Admin extends User {

    public Admin(int id, String name, String email, String password) {
        super(id, name, email, password, "admin");
        
    
    }

    public void manageUsers() {
        System.out.println("Admin can manage users.");
    }

    @Override
    public boolean login(String email, String password ) {
        System.out.println("Admin login attempt...");
        return super.login(email, password);
    }
}
