package application.entities;

import java.time.LocalDateTime;

public class Question {

    public static enum Type {
        TEXT,
        NUMBER,
        BOOLEAN
    }

    private String id;
    private Type type;
    private String text;
    private Answer answer;
    private LocalDateTime updatedAt;

    public Question(String id, Type type, String text) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.answer = new Answer(type, null);
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

    public void setText(String text) {
        this.text = text;
        this.updatedAt = LocalDateTime.now();
    }

    public Answer getAnswer() {
        return this.answer;
    }

    public void setAnswer(Answer answer) {
        this.updatedAt = LocalDateTime.now();
        this.answer = answer;
    }

    public boolean isAnswered() {
        return !(this.answer.getValue().isEmpty());
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    @Override
    public String toString() {
        return "Question [id=" + id + ", type=" + type + ", text=" + text + ", answer=" + answer + ", updatedAt="
                + updatedAt + "]";
    }
}
