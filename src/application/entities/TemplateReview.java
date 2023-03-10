package application.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import application.entities.TemplateQuestion.Type;

public class TemplateReview {

    public static TemplateQuestion wellbeingRateQuestion = new TemplateQuestion("1236d288-9b69-458e-8474-c58fcd35ad08",
            Type.NUMBER, "How would you rate your well-being today?", "Well-being Rating");
    public static TemplateQuestion productivityRateQuestion = new TemplateQuestion(
            "86f8f91a-17cb-4058-9dc2-5d439b3daa58", Type.NUMBER,
            "How would you rate your productivity today?", "Productivity Rating");
    public static TemplateQuestion dayRateQuestion = new TemplateQuestion("36276627-b507-41ff-b9f0-8bc7c9709986",
            Type.NUMBER, "How would you rate your day as a whole?", "Day Rating");

    private String id, name;
    private List<TemplateQuestion> templateQuestions;

    public static List<TemplateQuestion> getDefaultTemplateQuestions() {
        List<TemplateQuestion> defaultTemplateQuestions = new LinkedList<>();
        defaultTemplateQuestions.add(productivityRateQuestion);
        defaultTemplateQuestions.add(wellbeingRateQuestion);
        defaultTemplateQuestions.add(dayRateQuestion);
        return defaultTemplateQuestions;
    }

    public TemplateReview(String id, List<TemplateQuestion> templateQuestions, String name) {
        this.id = id == null ? UUID.randomUUID().toString() : id;
        this.templateQuestions = templateQuestions;
        this.name = name != null ? name : "Unnamed Template (" + id + ")";

    }

    public String getId() {
        return this.id;
    }

    public List<TemplateQuestion> getTemplateQuestions() {
        return this.templateQuestions;
    }

    public TemplateQuestion getTemplateQuestionById(String id) {
        for (TemplateQuestion q : this.templateQuestions) {
            if (q.getId().equals(id)) {
                return q;
            }
        }
        return null;
    }

    public void setTemplateQuestions(List<TemplateQuestion> questions) {
        this.templateQuestions = questions;
    }

    public void addQuestion(TemplateQuestion question) {
        this.templateQuestions.add(question);
    }

    public void removeQuestion(String id) {
        this.templateQuestions.removeIf(question -> question.getId().equals(id));
    }

    public String getDisplayName() {
        return this.name;
    }

    public void setDisplayName(String newName) {
        this.name = newName;
    }

}
