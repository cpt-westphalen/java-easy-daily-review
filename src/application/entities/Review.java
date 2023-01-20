package application.entities;

import java.time.LocalDateTime;
import java.util.List;

import application.entities.TemplateReview.Period;

public class Review {

    private String id;
    private Period period;
    private Integer dayRate;
    private Integer wellbeingRate;
    private Integer productivityRate;

    private LocalDateTime date;
    private List<Question> questions;

    public Review(String id, Period period, LocalDateTime date, List<Question> questions) {
        this.id = id;
        this.period = period;
        this.date = date;
        this.questions = questions;
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

    public Integer getDayRate() {
        return dayRate;
    }

    public void setDayRate(Integer dayRate) {
        this.dayRate = dayRate;
    }

    public Integer getWellbeingRate() {
        return wellbeingRate;
    }

    public void setWellbeingRate(Integer wellbeingRate) {
        this.wellbeingRate = wellbeingRate;
    }

    public Integer getProductivityRate() {
        return productivityRate;
    }

    public void setProductivityRate(Integer productivityRate) {
        this.productivityRate = productivityRate;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public List<Question> getQuestions() {
        return this.questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    public void removeQuestion(String id) {
        this.questions.removeIf(question -> question.getId() == id);
    }

    public Question getQuestionById(String id) {
        for (Question q : this.questions) {
            if (q.getId().equals(id)) {
                return q;
            }
        }
        return null;
    }

}
