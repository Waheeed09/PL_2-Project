package services;
import java.util.ArrayList;
import java.util.List;
import models.Admin;
import models.Lecturer;
import models.Result;
import models.Subject;
import models.User;
public class SubjectService {
 private List<Subject> subjects;
    private static final String SUBJECTS_FILE = "subjects.txt";
    public SubjectService() {
        this.subjects = new ArrayList<>();
        loadSubjects();
    }
    // Load subjects 
    private void loadSubjects() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SUBJECTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String subjectId = parts[0];
                    String subjectName = parts[1];
                    Integer lecturerId = parts.length > 2 && !parts[2].equals("null") ? Integer.parseInt(parts[2]) : null;
                    Subject subject = new Subject(subjectId, subjectName, lecturerId);
                    // Load enrolled students if Exist
                    if (parts.length > 3 && !parts[3].isEmpty()) {
                        String[] studentIds = parts[3].split(";");
                        for (String studentId : studentIds) {
                            if (!studentId.isEmpty()) {
                                subject.enrolledStudents.add(Integer.parseInt(studentId));
                            }
                        }
                    }
                    subjects.add(subject);
                }
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist start with empty list
            subjects = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Save subjects 
    private void saveSubjects() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SUBJECTS_FILE))) {
            for (Subject subject : subjects) {
                StringBuilder line = new StringBuilder();
                line.append(subject.subjectId).append(",");
                line.append(subject.subjectName).append(",");
                line.append(subject.lecturerId != null ? subject.lecturerId : "null").append(",");
                // Save students
                if (subject.enrolledStudents != null && !subject.enrolledStudents.isEmpty()) {
                    for (int i = 0; i < subject.enrolledStudents.size(); i++) {
                        line.append(subject.enrolledStudents.get(i));
                        if (i < subject.enrolledStudents.size() - 1) {
                            line.append(";");
                        }
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
            if (sub.subjectId.equals(subjectId)) return false;
        }
        subjects.add(new Subject(subjectId, subjectName, null));
        saveSubjects();
        return true;
    }
    
    public boolean assignSubjectToLecturer(String subjectId, int lecturerId) {
        Subject subject = null;
        for (Subject sub : subjects) {
            if (sub.subjectId.equals(subjectId)) {
                subject = sub;
                break;
            }
        }
        if (subject == null) return false;
        subject.lecturerId = lecturerId;
        saveSubjects();
        return true;
    }
    public boolean enrollStudentInSubject(int studentId, String subjectId) {
        Subject subject = null;
        for (Subject sub : subjects) {
            if (sub.subjectId.equals(subjectId)) {
                subject = sub;
                break;
            }
        }
        if (subject == null) return false;
        if (!subject.enrolledStudents.contains(studentId)) {
            subject.enrolledStudents.add(studentId);
        }
        saveSubjects();
        return true;
    }
    public List<Subject> getAllSubjects() {
        return new ArrayList<>(subjects);
    }
}
