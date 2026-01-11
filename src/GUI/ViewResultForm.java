package GUI;

import javax.swing.*;
import services.ResultService;
import services.FileManager;
import models.Result;
import models.Student;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;

public class ViewResultForm extends JFrame {

	private ResultService resultService = new ResultService();

	public ViewResultForm() {
		setTitle("View Results");
		setSize(640, 480);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		String bgPath = "D:\\Uni_Bedo\\Pl2\\Project_pl2\\PL_2-Project\\resources\\WhatsApp Image 2026-01-11 at 4.44.40 PM.jpeg";
		Image bg = UITheme.loadBackgroundImageAbsolute(bgPath);
		JPanel background = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (bg != null) g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
			}
		};
		background.setLayout(new GridBagLayout());
		background.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
		setContentPane(background);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8,8,8,8);
		gbc.fill = GridBagConstraints.BOTH;

		JLabel title = new JLabel("Student Results", JLabel.CENTER);
		title.setFont(UITheme.FONT_TITLE);
		title.setForeground(UITheme.TEXT_COLOR);
		gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=3; gbc.weightx=1.0; gbc.weighty=0.0;
		background.add(title, gbc);

		JComboBox<String> studentCombo = new JComboBox<>();
		studentCombo.setFont(UITheme.FONT_BODY);
		gbc.gridy=1; gbc.gridwidth=1; gbc.weightx=0.0; gbc.weighty=0.0; gbc.gridx=0;
		background.add(studentCombo, gbc);

		RoundedButton showBtn = new RoundedButton("Show"); showBtn.setBackground(UITheme.PRIMARY_COLOR);
		RoundedButton showAllBtn = new RoundedButton("Show All"); showAllBtn.setBackground(UITheme.PRIMARY_COLOR.darker());
		gbc.gridx=1; gbc.weightx=0.0; background.add(showBtn, gbc);
		gbc.gridx=2; background.add(showAllBtn, gbc);

		JTextArea area = new JTextArea();
		area.setEditable(false);
		area.setFont(UITheme.FONT_BODY);
		JScrollPane sp = new JScrollPane(area);
		gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=3; gbc.weighty=1.0; background.add(sp, gbc);

		RoundedButton registerBtn = new RoundedButton("Register"); registerBtn.setBackground(UITheme.BORDER_COLOR);
		gbc.gridy=3; gbc.gridwidth=1; gbc.weighty=0.0; gbc.gridx=0; background.add(registerBtn, gbc);

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
