package GUI;

import javax.swing.*;
import services.ResultService;
import services.FileManager;
import models.Result;
import models.Student;
import java.util.List;
import java.util.ArrayList;

public class ViewResultForm extends JFrame {

	private ResultService resultService = new ResultService();

	public ViewResultForm() {
		setTitle("View Results");
		setSize(500, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);

		JLabel lbl = new JLabel("Student:");
		lbl.setBounds(20, 20, 60, 25);
		add(lbl);

		JComboBox<String> studentCombo = new JComboBox<>();
		studentCombo.setBounds(90, 20, 200, 25);
		add(studentCombo);

		JButton showBtn = new JButton("Show");
		showBtn.setBounds(300, 20, 80, 25);
		add(showBtn);

		JButton showAllBtn = new JButton("Show All");
		showAllBtn.setBounds(390, 20, 80, 25);
		add(showAllBtn);

		JButton registerBtn = new JButton("Register");
		registerBtn.setBounds(20, 350, 100, 25);
		add(registerBtn);

		JTextArea area = new JTextArea();
		area.setEditable(false);
		JScrollPane sp = new JScrollPane(area);
		sp.setBounds(20, 60, 430, 280);
		add(sp);

		// populate students combo from data/students.txt
		ArrayList<Student> students = (ArrayList<Student>) FileManager.loadStudents();
		for (Student s : students) {
			studentCombo.addItem(s.getId() + " - " + s.getName());
		}

		showBtn.addActionListener(e -> {
			String selected = (String) studentCombo.getSelectedItem();
			if (selected == null || selected.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Please select a student.");
				return;
			}
			String sid = selected.split("\\s*-\\s*")[0];
			List<Result> results = resultService.getResultsByStudent(sid);
			if (results.isEmpty()) {
				area.setText("No results found for student: " + sid);
				return;
			}
			StringBuilder sb = new StringBuilder();
			for (Result r : results) {
				sb.append("Subject: ").append(r.getSubjectId())
				  .append(" | Grade: ").append(r.getGrade())
				  .append(" | Approved: ").append(r.isApproved())
				  .append("\n");
			}
			area.setText(sb.toString());
		});

		showAllBtn.addActionListener(e -> {
			List<Result> all = resultService.getAllResults();
			if (all.isEmpty()) {
				area.setText("No results available.");
				return;
			}
			StringBuilder sb = new StringBuilder();
			for (Result r : all) {
				sb.append("Student: ").append(r.getStudentId())
				  .append(" | Subject: ").append(r.getSubjectId())
				  .append(" | Grade: ").append(r.getGrade())
				  .append(" | Approved: ").append(r.isApproved())
				  .append("\n");
			}
			area.setText(sb.toString());
		});

		registerBtn.addActionListener(e -> {
			String name = JOptionPane.showInputDialog(this, "Name:");
			if (name == null || name.trim().isEmpty()) return;
			String email = JOptionPane.showInputDialog(this, "Email:");
			if (email == null) return;
			String pwd = JOptionPane.showInputDialog(this, "Password:");
			if (pwd == null) return;

			// load current students, add new, save
			ArrayList<Student> cur = (ArrayList<Student>) FileManager.loadStudents();
			int maxId = 0;
			for (Student s : cur) if (s.getId() > maxId) maxId = s.getId();
			Student ns = new Student(maxId + 1, name.trim(), email.trim(), pwd);
			cur.add(ns);
			FileManager.saveStudents(cur);
			studentCombo.addItem(ns.getId() + " - " + ns.getName());
			JOptionPane.showMessageDialog(this, "Registered student ID: " + ns.getId());
		});

		setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new ViewResultForm());
	}
}

