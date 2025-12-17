package GUI;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.*;
import services.*;
import services.FileManager;
public class AdminDashboard {
    private AdminService adminService;
    private StudentService studentService;
    private TextArea outputArea;
    @Override
    public void start(Stage stage) {
        initServices();
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                userTab(),
                subjectTab(),
                resultTab(),
                studentTab()
        );
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200);
        VBox root = new VBox(10, tabPane, new Label("System Output:"), outputArea);
        root.setPadding(new Insets(10));
        stage.setTitle("Admin Dashboard");
        stage.setScene(new Scene(root, 900, 600));
        stage.show();
    }
    // ================= INIT =================
    private void initServices() {
        adminService = new AdminService();
        studentService = new StudentService();
        adminService.setUsers(FileManager.loadUsers());
        adminService.setSubjects(FileManager.loadSubjects());
        adminService.setResults(FileManager.loadResults());
        studentService.loadStudents();
    }
    // ================= USER TAB =================
    private Tab userTab() {
        TextField id = new TextField();
        TextField name = new TextField();
        TextField email = new TextField();
        PasswordField pass = new PasswordField();
        ComboBox<String> role = new ComboBox<>();
        role.getItems().addAll("admin", "student", "lecturer");
        Button add = new Button("Add User");
        Button list = new Button("List Users");
        Button delete = new Button("Delete User");
        add.setOnAction(e -> {
            try {
                User u = new User(
                        Integer.parseInt(id.getText()),
                        name.getText(),
                        email.getText(),
                        pass.getText(),
                        role.getValue()
                );
                adminService.addUser(u);
                log("User added successfully");
            } catch (Exception ex) {
                log("Error adding user");
            }
        });
        list.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            for (User u : adminService.getUsers()) sb.append(u).append('\n');
            log(sb.toString());
        });
        delete.setOnAction(e -> {
            adminService.deleteUser(Integer.parseInt(id.getText()));
            log("User deleted");
        });
        GridPane grid = grid();
        grid.addRow(0, new Label("ID"), id);
        grid.addRow(1, new Label("Name"), name);
        grid.addRow(2, new Label("Email"), email);
        grid.addRow(3, new Label("Password"), pass);
        grid.addRow(4, new Label("Role"), role);
        grid.addRow(5, add, list);
        grid.add(delete, 1, 6);
        return new Tab("Users", grid);
    }
    // ================= SUBJECT TAB =================
    private Tab subjectTab() {
        TextField sid = new TextField();
        TextField sname = new TextField();
        Button add = new Button("Add Subject");
        Button list = new Button("List Subjects");
        add.setOnAction(e -> {
            adminService.addSubject(new Subject(sid.getText(), sname.getText()));
            log("Subject added");
        });
        list.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            for (Subject s : adminService.getSubjects()) sb.append(s).append('\n');
            log(sb.toString());
        });
        GridPane grid = grid();
        grid.addRow(0, new Label("Subject ID"), sid);
        grid.addRow(1, new Label("Subject Name"), sname);
        grid.addRow(2, add, list);

        return new Tab("Subjects", grid);
    }
    // ================= RESULT TAB =================
    private Tab resultTab() {
        Button approve = new Button("Approve All Results");
        Button list = new Button("List Results");
        approve.setOnAction(e -> {
            adminService.approveAllResults();
            log("All results approved");
        });
        list.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            for (models.Result r : adminService.getResults()) sb.append(r).append('\n');
            log(sb.toString());
        });
        VBox box = new VBox(10, approve, list);
        box.setPadding(new Insets(10));

        return new Tab("Results", box);
    }
    // ================= STUDENT TAB =================
    private Tab studentTab() {
        Button list = new Button("List Students");

        list.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            for (Student s : studentService.loadStudents()) sb.append(s).append('\n');
            log(sb.toString());
        });

        VBox box = new VBox(10, list);
        box.setPadding(new Insets(10));

        return new Tab("Students", box);
    }
    // ================= HELPERS =================
    private GridPane grid() {
        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(10);
        g.setPadding(new Insets(10));
        return g;
    }
    private void log(String msg) {
        outputArea.appendText(msg + "\n");
    }
    @Override
    public void stop() {
        FileManager.saveUsers(adminService.getUsers());
        FileManager.saveSubjects(adminService.getSubjects());
        FileManager.saveResults(adminService.getResults());
    }
    public static void main(String[] args) {
        launch(args);
    }
}
}
