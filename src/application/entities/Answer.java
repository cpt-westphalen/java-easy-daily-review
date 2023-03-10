package application.entities;

import java.time.LocalDate;

import application.entities.TemplateQuestion.Type;

public class Answer {
    private Type type;
    private String value;
    private LocalDate updatedAt;

    public Answer(Type type, String value) {
        this.type = type;
        this.value = value;
        this.updatedAt = LocalDate.now();
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
        this.updatedAt = LocalDate.now();
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

    public LocalDate getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(LocalDate date) {
        this.updatedAt = date;
    }
}
