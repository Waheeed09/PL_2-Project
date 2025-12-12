package GUI;

import javax.swing.*;

public class StudentDashboard extends JFrame {

    public StudentDashboard(int studentId) {

        setTitle("Student Dashboard");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null); // أبسط طريقة لترتيب العناصر

        // زرار واحد يفتح TakeExamForm
        JButton takeExamBtn = new JButton("Take Exam");
        takeExamBtn.setBounds(80, 50, 140, 40);
        add(takeExamBtn);

        takeExamBtn.addActionListener(e -> {
            new TakeExamForm(studentId); // فتح فورم الامتحان
        });

        setVisible(true);
    }

    // لتشغيل الـ Dashboard مباشرة (اختياري للمبتدئين)
    public static void main(String[] args) {
        new StudentDashboard(1); // رقم الطالب كمثال
    }
}
