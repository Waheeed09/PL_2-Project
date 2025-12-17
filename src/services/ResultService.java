package services;

import java.util.ArrayList;
import java.util.List;
import models.Result;

public class ResultService {

    private List<Result> results;

    public ResultService() {
        this.results = new ArrayList<>();
    }

    // ================= Add Result =================
    public void addResult(Result result) {
        if (result != null) {          
            if (result.getGrade() >= 50) {
                result.setApproved(true);
            }
            results.add(result);
        }
    }

    // ================= Get All Results =================
    public List<Result> getAllResults() {
        return results;
    }

    // ================= Find Result by Student =================
    public List<Result> getResultsByStudent(String studentId) {
        List<Result> studentResults = new ArrayList<>();
        for (Result r : results) {
            if (r.getStudentId().equals(studentId)) {
                studentResults.add(r);
            }
        }
        return studentResults;
    }

    // ================= Find Result by Subject =================
    public List<Result> getResultsBySubject(String subjectId) {
        List<Result> subjectResults = new ArrayList<>();
        for (Result r : results) {
            if (r.getSubjectId().equals(subjectId)) {
                subjectResults.add(r);
            }
        }
        return subjectResults;
    }

    // ================= Update Result =================
    public boolean updateResult(String studentId, String subjectId, double newGrade) {
        for (Result r : results) {
            if (r.getStudentId().equals(studentId) && r.getSubjectId().equals(subjectId)) {
                r.setGrade(newGrade);
                r.setApproved(newGrade >= 50);
                return true;
            }
        }
        return false; // لم يتم العثور على النتيجة
    }

    // ================= Remove Result =================
    public boolean removeResult(String studentId, String subjectId) {
        return results.removeIf(r -> r.getStudentId().equals(studentId) &&
                                     r.getSubjectId().equals(subjectId));
    }

    // ================= Display All =================
    public void displayAllResults() {
        for (Result r : results) {
            System.out.println(r);
        }
    }
}

