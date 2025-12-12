package services;

public class AdminService {
    private DataStore dataStore;
    private List<Student> students;
    private List<Lecturer> lecturers;
    private List<Admin> admins;
    private List<Subject> subjects;
    private List<Grade> grades;
    private Admin currentAdmin;
    
    public AdminService() {
        this.dataStore = new DataStore();
        loadAllData();
    }
    
    private void loadAllData() {
        this.students = dataStore.loadStudents();
        this.lecturers = dataStore.loadLecturers();
        this.admins = dataStore.loadAdmins();
        this.subjects = dataStore.loadSubjects();
        this.grades = dataStore.loadGrades();
    }
    
    private void saveAllData() {
        dataStore.saveStudents(students);
        dataStore.saveLecturers(lecturers);
        dataStore.saveAdmins(admins);
        dataStore.saveSubjects(subjects);
        dataStore.saveGrades(grades);
    }
           
    public boolean authenticateAdmin(String username, String password) {
        for (Admin admin : admins) {
            if (admin.username.equals(username) && admin.password.equals(password)) {
                this.currentAdmin = admin;
                return true;
            }
        }
        return false;
    }
    
    public boolean updateAdminCredentials(String newUsername, String newPassword) {
        if (currentAdmin == null) return false;
        currentAdmin.username = newUsername;
        currentAdmin.password = newPassword;
        dataStore.saveAdmins(admins);
        return true;
    }
    
    public boolean addStudent(int id, String username, String password, String name) {
        for (Student s : students) {
            if (s.id==(id)) return false;
        }
        students.add(new Student(id, username, password, name));
        dataStore.saveStudents(students);
        return true;
    }
    
    public boolean deleteStudent(int id) {
        boolean removed = students.removeIf(s -> s.id==(id));
        if (removed) {
            for (Subject subject : subjects) {
                subject.enrolledStudents.remove(id);
            }
            grades.removeIf(g -> g.studentId.equals(id));
            saveAllData();
        }
        return removed;
    }
    
    public boolean updateStudent(int id, String newName, String newUsername) {
        for (Student s : students) {
            if (s.id==(id)) {
                s.name = newName;
                s.username = newUsername;
                dataStore.saveStudents(students);
                return true;
            }
        }
        return false;
    }
    public List<Student> listAllStudents() {
        return new ArrayList<>(students);
    }
    public Student searchStudent(int id) {
        for (Student s : students) {
            if (s.id==(id)) return s;
        }
        return null;
    }
    public boolean addLecturer(int id, String username, String password, String name) {
        for (Lecturer l : lecturers) {
            if (l.id==(id)) return false;
        }
        lecturers.add(new Lecturer(id, username, password, name));
        dataStore.saveLecturers(lecturers);
        return true;
    }
    public boolean deleteLecturer(int id) {
        boolean removed = lecturers.removeIf(l -> l.id==(id));
        if (removed) {
            for (Subject subject : subjects) {
                if (subject.lecturerId==(id)) {
                    subject.lecturerId = null;
                }
            }
            saveAllData();
        }
        return removed;
    }
    public boolean updateLecturer(int id, String newName, String newUsername) {
        for (Lecturer l : lecturers) {
            if (l.id==(id)) {
                l.name = newName;
                l.username = newUsername;
                dataStore.saveLecturers(lecturers);
                return true;
            }
        }
        return false;
    }
    public List<Lecturer> listAllLecturers() {
        return new ArrayList<>(lecturers);
    }
    public Lecturer searchLecturer(int id) {
        for (Lecturer l : lecturers) {
            if (l.id==(id)) return l;
        }
        return null;
    }
    public boolean addSubject(String subjectId, String subjectName) {
        for (Subject sub : subjects) {
            if (sub.subjectId.equals(subjectId)) return false;
        }
        subjects.add(new Subject(subjectId, subjectName, null));
        dataStore.saveSubjects(subjects);
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
        Lecturer lecturer = null;
        for (Lecturer l : lecturers) {
            if (l.id==(lecturerId)) {
                lecturer = l;
                break;
            }
        }
        if (subject == null || lecturer == null) return false;
        subject.lecturerId = lecturerId;
        if (!lecturer.assignedSubjects.contains(subjectId)) {
            lecturer.assignedSubjects.add(subjectId);
        }
        saveAllData();
        return true;
    }
    public boolean enrollStudentInSubject(int studentId, String subjectId) {
        Student student = null;
        for (Student s : students) {
            if (s.id==(studentId)) {
                student = s;
                break;
            }
        }
        Subject subject = null;
        for (Subject sub : subjects) {
            if (sub.subjectId.equals(subjectId)) {
                subject = sub;
                break;
            }
        }
        if (student == null || subject == null) return false;
        
        if (!subject.enrolledStudents.contains(studentId)) {
            subject.enrolledStudents.add(studentId);
        }
        if (!student.enrolledSubjects.contains(subjectId)) {
            student.enrolledSubjects.add(subjectId);
        }
        
        saveAllData();
        return true;
    }
    public List<Grade> getPendingGrades() {
        List<Grade> pending = new ArrayList<>();
        for (Grade g : grades) {
            if (!g.approved) {
                pending.add(g);
            }
        }
        return pending;
    }
    public boolean approveGrade(String studentId, String subjectId) {
        if (currentAdmin == null) return false;
        for (Grade g : grades) {
            if (g.studentId.equals(studentId) && g.subjectId.equals(subjectId) && !g.approved) {
                g.approved = true;
                g.approvedBy = currentAdmin.id;
                for (Student s : students) {
                    if (s.id==(studentId)) {
                        s.grades.put(subjectId, g.score);
                        break;
                    }
                }
                saveAllData();
                return true;
            }
        }
        return false;
    }
    public boolean publishGrades(String subjectId) {
        if (currentAdmin == null) return false;
        boolean published = false;
        for (Grade g : grades) {
            if (g.subjectId.equals(subjectId) && !g.approved) {
                g.approved = true;
                g.approvedBy = currentAdmin.id;
                for (Student s : students) {
                    if (s.id==(studentId)) {
                        s.grades.put(subjectId, g.score);
                    }
                }
                published = true;
            }
        }
        if (published) {
            saveAllData();
        }
        return published;
    }
}
}
