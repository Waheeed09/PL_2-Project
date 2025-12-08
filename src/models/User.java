package models;

public abstract class User {
    protected String userId;
    protected String name;
    protected String password;
    protected String email;
    protected String userType; // "STUDENT", "LECTURER", "ADMIN"
    
    public User(String userId, String name, String password, String email, String userType) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.email = email;
        this.userType = userType;
    }
    
    // Abstract method - كل user type هيعمله implement
    public abstract boolean hasPermission(String action);
    
    // Getters & Setters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getUserType() { return userType; }
    
    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
}