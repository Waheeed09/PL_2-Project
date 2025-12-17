package services;

import java.util.ArrayList;
import java.util.List;
import java.io.*;
import models.Subject;
public class SubjectService {
    
    private List<Subject> subjects;
    private static final String SUBJECTS_FILE = "subjects.txt";
    public SubjectService() {
        this.subjects = new ArrayList<>();
        loadSubjects();
    }
    private void loadSubjects() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SUBJECTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String subjectId = parts[0];
                    String subjectName = parts[1];
                    Subject subject = new Subject(subjectId, subjectName);
                    if (parts.length > 2 && !parts[2].equals("null")) {
                        try {
                            subject.setLecturerId(Integer.parseInt(parts[2]));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    if (parts.length > 3 && !parts[3].isEmpty()) {
                        String[] studentIds = parts[3].split(";");
                        for (String studentId : studentIds) {
                            subject.getEnrolledStudents().add(studentId);
                        }
                    }
                    subjects.add(subject);
                }
            }
        } catch (FileNotFoundException e) {
            subjects = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveSubjects() {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(SUBJECTS_FILE))) {
            for (Subject subject : subjects) {
                StringBuilder line = new StringBuilder();
                line.append(subject.getSubjectId()).append(",");
                line.append(subject.getSubjectName()).append(",");
                int lid = subject.getLecturerId();
                line.append(lid != 0 ? String.valueOf(lid) : "null").append(",");
                List<String> students = subject.getEnrolledStudents();
                for (int i = 0; i < students.size(); i++) {
                    line.append(students.get(i));
                    if (i < students.size() - 1) {
                        line.append(";");
                    }
                }
                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean addSubject(String subjectId, String subjectName) {
        for (Subject sub : subjects) {
            if (sub.getSubjectId().equals(subjectId)) {
                return false;
            }
        }
        subjects.add(new Subject(subjectId, subjectName));
        saveSubjects();
        return true;
    }
    public boolean assignSubjectToLecturer(String subjectId, int lecturerId) {
        for (Subject subject : subjects) {
            if (subject.getSubjectId().equals(subjectId)) {
                subject.setLecturerId(lecturerId);
                saveSubjects();
                return true;
            }
        }
        return false;
    }
    public boolean enrollStudentInSubject(int studentId, String subjectId) {
        for (Subject subject : subjects) {
            if (subject.getSubjectId().equals(subjectId)) {
                if (!subject.getEnrolledStudents().contains(String.valueOf(studentId))) {
                    subject.getEnrolledStudents().add(String.valueOf(studentId));
                    saveSubjects();
                }
                return true;
            }
        }
        return false;
    }
    public List<Subject> getAllSubjects() {
        return new ArrayList<>(subjects);
    }
}