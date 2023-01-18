package application.entities;

import java.time.LocalDateTime;

import application.entities.Question.Type;

public class Answer {
    private Type type;
    private String value;
    private LocalDateTime updatedAt;

    public Answer(Type type, String value) {
        this.type = type;
        this.value = null;
        this.updatedAt = LocalDateTime.now();
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public Integer getValueAsInteger() {
        return Integer.valueOf(this.value);
    }

    public void setValue(String value) {
        this.updatedAt = LocalDateTime.now();
        this.value = value;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }
}
