package application.entities;

import java.util.List;

public class TemplateReview {

    public static enum Period {
        DAILY,
        WEEKLY,
        QUARTERLY,
        YEARLY
    };

    public static String rateDayQuestionText = "How would you rate your day as a whole? (integer, 0-100)";

    private String id;
    private Period period;
    private List<TemplateQuestion> templateQuestions;

    public TemplateReview(String id, Period period, List<TemplateQuestion> templateQuestions) {
        this.id = id;
        this.period = period;
        this.templateQuestions = templateQuestions;
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

    @Override
    public String toString() {
        return "TemplateReview [\n\tid=" + id + ", \n\tperiod=" + period + ", \n\ttemplateQuestions="
                + templateQuestions
                + "\n]";
    }

}
