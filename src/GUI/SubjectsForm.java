package GUI;
import javax.swing.*;
import services.SubjectService;
import models.Subject;
import java.util.List;
public class SubjectsForm {
    private SubjectService subjectService = new SubjectService();
    public SubjectsForm() {
        JFrame frame = new JFrame("Subjects");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        JTextArea area = new JTextArea();
        area.setEditable(false);
        JScrollPane sp = new JScrollPane(area);
        sp.setBounds(20, 20, 350, 200);
        frame.add(sp);
        List<Subject> subjects = subjectService.getAllSubjects();
        StringBuilder sb = new StringBuilder();
        for (Subject s : subjects) {
            sb.append(s).append("\n");
        }
        area.setText(sb.toString());
        frame.setVisible(true);
    }
    
}
