package services;

import java.io.*;
import java.util.*;
import models.User;
import models.Subject;
import models.Result;

public class FileManager {

    private static final String USERS_FILE = "data/users.txt";
    private static final String SUBJECTS_FILE = "data/subjects.txt";
    private static final String RESULTS_FILE = "data/results.txt";

    // ------------------- Users -------------------
    public static void saveUsers(List<User> users) {
        try (FileWriter fw = new FileWriter(USERS_FILE)) {
            for (User u : users) {
                fw.write(u.getId() + "," + u.getName() + "," + u.getEmail() + "," + u.getPassword() + "," + u.getRole() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue;
                int id = Integer.parseInt(parts[0]);
                users.add(new User(id, parts[1], parts[2], parts[3], parts[4]));
            }
        } catch (IOException e) {
            System.out.println("No users file found. Starting fresh.");
        }
        return users;
    }

    // ------------------- Subjects -------------------
    public static void saveSubjects(List<Subject> subjects) {
        try (FileWriter fw = new FileWriter(SUBJECTS_FILE)) {
            for (Subject s : subjects) {
                fw.write(s.getSubjectId() + "," + s.getSubjectName() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving subjects: " + e.getMessage());
        }
    }

    public static List<Subject> loadSubjects() {
        List<Subject> subjects = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(SUBJECTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue;
                subjects.add(new Subject(parts[0], parts[1]));
            }
        } catch (IOException e) {
            System.out.println("No subjects file found. Starting fresh.");
        }
        return subjects;
    }

    // ------------------- Results -------------------
    public static void saveResults(List<Result> results) {
        try (FileWriter fw = new FileWriter(RESULTS_FILE)) {
            for (Result r : results) {
                fw.write(r.getStudentId() + "," + r.getSubjectId() + "," + r.getGrade() + "," + r.isApproved() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving results: " + e.getMessage());
        }
    }

    public static List<Result> loadResults() {
        List<Result> results = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(RESULTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;
                Result r = new Result(parts[0], parts[1], Double.parseDouble(parts[2]));
                r.setApproved(Boolean.parseBoolean(parts[3]));
                results.add(r);
            }
        } catch (IOException e) {
            System.out.println("No results file found. Starting fresh.");
        }
        return results;
    }
}
