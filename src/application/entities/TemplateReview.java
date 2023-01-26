package application.entities;

import java.util.List;

import application.entities.TemplateQuestion.Type;

public class TemplateReview {

    public static enum Period {
        DAILY,
        WEEKLY,
    };

    public static TemplateQuestion wellbeingRateQuestion = new TemplateQuestion("1236d288-9b69-458e-8474-c58fcd35ad08",
            Type.NUMBER, "How would you rate your well-being today?");
    public static TemplateQuestion productivityRateQuestion = new TemplateQuestion(
            "86f8f91a-17cb-4058-9dc2-5d439b3daa58", Type.NUMBER,
            "How would you rate your productivity today?");
    public static TemplateQuestion dayRateQuestion = new TemplateQuestion("36276627-b507-41ff-b9f0-8bc7c9709986",
            Type.NUMBER, "How would you rate your day as a whole?");

    private String id, name;
    private Period period;
    private List<TemplateQuestion> templateQuestions;

    public TemplateReview(String id, Period period, List<TemplateQuestion> templateQuestions, String name) {
        this.id = id;
        this.period = period;
        this.templateQuestions = templateQuestions;
        this.name = name != null ? name : "Unnamed Template (" + id + ")";

        boolean injectDayRate = true, injectWellbeingRate = true,
                injectProductivityRate = true;

        for (TemplateQuestion question : this.templateQuestions) {
            if (question.getId().equals(productivityRateQuestion.getId())) {
                injectProductivityRate = false;
            }
            if (question.getId().equals(wellbeingRateQuestion.getId())) {
                injectWellbeingRate = false;
            }
            if (question.getId().equals(dayRateQuestion.getId())) {
                injectDayRate = false;
            }
        }
        if (injectProductivityRate)
            this.templateQuestions.add(productivityRateQuestion);
        if (injectWellbeingRate)
            this.templateQuestions.add(wellbeingRateQuestion);
        if (injectDayRate)
            this.templateQuestions.add(dayRateQuestion);
    }

    public String getId() {
        return this.id;
    }

    public Period getPeriod() {
        return this.period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public List<TemplateQuestion> getTemplateQuestions() {
        return this.templateQuestions;
    }

    public void setTemplateQuestions(List<TemplateQuestion> questions) {
        this.templateQuestions = questions;
    }

    public void addQuestion(TemplateQuestion question) {
        this.templateQuestions.add(question);
    }

    public void removeQuestion(String id) {
        this.templateQuestions.removeIf(question -> question.getId() == id);
    }

    public TemplateQuestion getTemplateQuestionById(String id) {
        for (TemplateQuestion q : this.templateQuestions) {
            if (q.getId() == id) {
                return q;
            }
        }
        return null;
    }

    public String getDisplayName() {
        return this.name;
    }

    public void setDisplayName(String newName) {
        this.name = newName;
    }

    @Override
    public String toString() {
        return "TemplateReview [\n\tid=" + id + ", \n\tperiod=" + period + ", \n\ttemplateQuestions="
                + templateQuestions
                + "\n]";
    }

}
