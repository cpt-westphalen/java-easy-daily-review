package application.entities;

import java.time.LocalDateTime;

import application.entities.TemplateQuestion.Type;

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
        try {
            return Integer.valueOf(this.value);
        } catch (Exception e) {
            return null;
        }
    }

    public void setValue(String value) {
        this.updatedAt = LocalDateTime.now();
        if (value == null || value.isBlank() || value.isEmpty()) {
            this.value = null;
            return;
        }
        if (this.type.equals(Type.BOOLEAN)) {
            this.value = value.toLowerCase().startsWith("y") ? "Yes" : "No";
            return;
        }
        if (this.type.equals(Type.NUMBER)) {
            try {
                Integer.parseInt(value);
            } catch (Exception e) {
                this.value = null;
                return;
            }
        }
        this.value = value.trim();
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }
}
