package models;

// Interface for polymorphism
public interface Question {
    String getQuestionId();
    String getQuestionText();
    int getMarks();
    boolean checkAnswer(String answer);
    String getCorrectAnswer();
    String getQuestionType();
}

// Multiple Choice Question
class MultipleChoiceQuestion implements Question {
    private String questionId;
    private String questionText;
    private String[] options;
    private String correctAnswer;
    private int marks;
    
    public MultipleChoiceQuestion(String id, String text, String[] options, 
                                 String correctAnswer, int marks) {
        this.questionId = id;
        this.questionText = text;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.marks = marks;
    }
    
    @Override
    public String getQuestionId() { return questionId; }
    
    @Override
    public String getQuestionText() { return questionText; }
    
    @Override
    public int getMarks() { return marks; }
    
    @Override
    public boolean checkAnswer(String answer) {
        return answer != null && answer.equalsIgnoreCase(correctAnswer);
    }
    
    @Override
    public String getCorrectAnswer() { return correctAnswer; }
    
    @Override
    public String getQuestionType() { return "MCQ"; }
    
    public String[] getOptions() { return options; }
}

// True/False Question
class TrueFalseQuestion implements Question {
    private String questionId;
    private String questionText;
    private boolean correctAnswer;
    private int marks;
    
    public TrueFalseQuestion(String id, String text, boolean correctAnswer, int marks) {
        this.questionId = id;
        this.questionText = text;
        this.correctAnswer = correctAnswer;
        this.marks = marks;
    }
    
    @Override
    public String getQuestionId() { return questionId; }
    
    @Override
    public String getQuestionText() { return questionText; }
    
    @Override
    public int getMarks() { return marks; }
    
    @Override
    public boolean checkAnswer(String answer) {
        return answer != null && 
               answer.equalsIgnoreCase(String.valueOf(correctAnswer));
    }
    
    @Override
    public String getCorrectAnswer() { return String.valueOf(correctAnswer); }
    
    @Override
    public String getQuestionType() { return "TF"; }
}

// Short Answer Question
class ShortAnswerQuestion implements Question {
    private String questionId;
    private String questionText;
    private String correctAnswer;
    private int marks;
    
    public ShortAnswerQuestion(String id, String text, String correctAnswer, int marks) {
        this.questionId = id;
        this.questionText = text;
        this.correctAnswer = correctAnswer;
        this.marks = marks;
    }
    
    @Override
    public String getQuestionId() { return questionId; }
    
    @Override
    public String getQuestionText() { return questionText; }
    
    @Override
    public int getMarks() { return marks; }
    
    @Override
    public boolean checkAnswer(String answer) {
        return answer != null && 
               answer.trim().equalsIgnoreCase(correctAnswer.trim());
    }
    
    @Override
    public String getCorrectAnswer() { return correctAnswer; }
    
    @Override
    public String getQuestionType() { return "SHORT"; }
}