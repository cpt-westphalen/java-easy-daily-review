package application.entities;

import java.time.LocalDate;
import java.util.List;

import application.entities.TemplateReview.Period;

public class Review {

    private String authorId;
    private String id;
    private Period period;
    private Integer dayRate;
    private Integer wellbeingRate;
    private Integer productivityRate;

    private LocalDate date;
    private List<Question> questions;

    public Review(String authorId, String reviewId, Period period, LocalDate date, List<Question> questions) {
        this.authorId = authorId;
        this.id = reviewId;
        this.period = period;
        this.date = date;
        this.questions = questions;
    }

    public String getId() {
        return this.id;
    }

    public String getAuthorId() {
        return authorId;
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

    public LocalDate getDate() {
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
        this.questions.removeIf(question -> question.getId().equals(id));
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
