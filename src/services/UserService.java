package services;

import java.util.ArrayList;

public class UserService {

    private ArrayList<User> users = new ArrayList<>();

    public UserService() {

        users.add(new Admin(1, "Admin", "admin@mail.com", "admin", "1234"));
        users.add(new Student(2,"IBRAHIM","IB515@mail.com","St","0007"));
    }

    public User login(String username, String password) {
        for (User u : users) {
            if (u.getUsername() != null &&
                    u.getUsername().equals(username) &&
                    u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

}






