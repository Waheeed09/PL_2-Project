package GUI;

import models.Result;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class ViewResultForm extends JFrame {

    private JTable resultTable;
    private DefaultTableModel tableModel;

    public ViewResultForm(List<Result> results) {
        setTitle("View Results");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] columns = {"ID", "Name", "Email", "Role", "Course", "Marks", "Grade"};
        tableModel = new DefaultTableModel(columns, 0);
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);

        loadResults(results);
    }

    private void loadResults(List<Result> results) {
        for (Result r : results) {
            Object[] rowData = {
                    r.getId(),
                    r.getName(),
                    r.getEmail(),
                    r.getRole(),
                    r.getCourse(),
                    r.getMarks(),
                    r.calculateGrade()
            };
            tableModel.addRow(rowData);
        }
    }

    public static void main(String[] args) {
        List<Result> results = new ArrayList<>();
        results.add(new Result(1, "Ahmed", "ahmed@mail.com", "123", "Student", "Math", 85));
        results.add(new Result(2, "Mona", "mona@mail.com", "456", "Student", "Physics", 92));

        SwingUtilities.invokeLater(() -> {
            ViewResultForm form = new ViewResultForm(results);
            form.setVisible(true);
        });
    }
}
