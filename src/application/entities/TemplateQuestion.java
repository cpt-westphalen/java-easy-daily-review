package application.entities;

public class TemplateQuestion {

    public static enum Type {
        TEXT,
        NUMBER,
        BOOLEAN
    }

    protected String id;
    protected Type type;
    protected String text;

    public TemplateQuestion(String id, Type type, String text) {
        this.id = id;
        this.type = type;
        this.text = text;
    }

    public String getId() {
        return this.id;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
