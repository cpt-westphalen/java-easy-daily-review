package application.entities;

import java.time.LocalDateTime;

import application.entities.TemplateQuestion.Type;

public class Question {

    private String id;
    private Type type;
    private String text;
    private Answer answer;
    private LocalDateTime updatedAt;

    public Question(TemplateQuestion templateQuestion, Answer answer, LocalDateTime updatedAt) {
        this.id = templateQuestion.id;
        this.type = templateQuestion.type;
        this.text = templateQuestion.text;
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        this.answer = answer != null ? answer : new Answer(templateQuestion.type, null);
    }

    public String getId() {
        return this.id;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.answer.setType(type);
        this.type = type;
    }

    public String getText() {
        return this.text;
    }

    public Answer getAnswer() {
        return this.answer;
    }

    public void setAnswer(Answer answer) {
        if (answer != null) {
            this.updatedAt = LocalDateTime.now();
            this.answer = answer;
        }
    }

    public boolean isAnswered() {
        return !(this.answer.getValue() == null || this.answer.getValue().isEmpty());
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(LocalDateTime date) {
        this.updatedAt = date;
    }

    @Override
    public String toString() {
        return "Question [\n\tid=" + id + ", \n\ttype=" + type + ", \n\ttext=" + text + ", \n\tanswer=" + answer
                + ", \n\tupdatedAt="
                + updatedAt + "\n]";
    }
}
