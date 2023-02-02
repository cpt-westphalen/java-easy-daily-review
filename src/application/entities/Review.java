package application.entities;

import java.time.LocalDate;
import java.util.List;

public class Review {

    private String authorId;
    private String id;
    private Rates rates;

    private LocalDate date;
    private List<Question> questions;

    public Review(String authorId, String reviewId, LocalDate date, List<Question> questions) {
        this.authorId = authorId;
        this.id = reviewId;
        this.date = date;
        this.questions = questions;

        Integer dayRate = this.getQuestionById(
                "36276627-b507-41ff-b9f0-8bc7c9709986")
                .getAnswer().getValueAsInteger();
        Integer wellbeingRate = this
                .getQuestionById(
                        "1236d288-9b69-458e-8474-c58fcd35ad08")
                .getAnswer().getValueAsInteger();
        Integer productivityRate = this
                .getQuestionById(
                        "86f8f91a-17cb-4058-9dc2-5d439b3daa58")
                .getAnswer().getValueAsInteger();

        this.rates = new Rates(productivityRate, wellbeingRate, dayRate);
    }

    public String getId() {
        return this.id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public Integer getDayRate() {
        return rates.getDayRate();
    }

    public void setDayRate(Integer dayRate) {
        this.rates.setDayRate(dayRate);
    }

    public Integer getWellbeingRate() {
        return rates.getWellbeingRate();
    }

    public void setWellbeingRate(Integer wellbeingRate) {
        this.rates.setWellbeingRate(wellbeingRate);
    }

    public Integer getProductivityRate() {
        return rates.getProductivityRate();
    }

    public void setProductivityRate(Integer productivityRate) {
        this.rates.setProductivityRate(productivityRate);
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
