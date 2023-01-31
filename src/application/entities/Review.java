package application.entities;

import java.time.LocalDate;
import java.util.List;

public class Review {

    private String authorId;
    private String id;
    private Integer dayRate;
    private Integer wellbeingRate;
    private Integer productivityRate;

    private LocalDate date;
    private List<Question> questions;

    public Review(String authorId, String reviewId, LocalDate date, List<Question> questions) {
        this.authorId = authorId;
        this.id = reviewId;
        this.date = date;
        this.questions = questions;
    }

    public String getId() {
        return this.id;
    }

    public String getAuthorId() {
        return authorId;
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
