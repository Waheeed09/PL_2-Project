public class Question {
    public String examId;
    public String text;
    public String correct;
    protected int id;
    protected String text1;

    public Question(){ }

    public Question(String examId, String text, String correct) {
        this.examId = examId;
        this.text = text;
        this.correct = correct;
    }


    public Question(int id, String text1) {
        this.id = id;
        this.text1 = text1;
    }

    // إضافة سؤال
    public void addQuestion(Exam exam, Question1 q) {
        exam.questions.add(q);
    }

    // تعديل سؤال
    public void updateQuestion(Exam exam, int id, String newText) {
        for (int i = 0; i < exam.questions.size(); i++) {
            if (exam.questions.get(i).id == id) {
                exam.questions.get(i).text = newText;
                break;
            }
        }
    }

    // حذف سؤال
    public void deleteQuestion(Exam exam, int id) {
        for (int i = 0; i < exam.questions.size(); i++) {
            if (exam.questions.get(i).id == id) {
                exam.questions.remove(i);
                break;
            }
        }
    }

    // عرض الأسئلة
    public void showQuestions(Exam exam) {
        for (int i = 0; i < exam.questions.size(); i++) {
            Question q = exam.questions.get(i);
            System.out.println(q.id + " - " + q.text);
        }
    }

}
