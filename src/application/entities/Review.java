package application.entities;

import java.time.LocalDateTime;
import java.util.List;

import application.entities.Template.Period;

public class Review {

    private String id;
    private Period period;
    private Integer rate;
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

    public Integer getRate() {
        return this.rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
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
            if (q.getId() == id) {
                return q;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Review [id=" + id + ", period=" + period + ", rate=" + rate + ", date=" + date + ", questions="
                + questions + "]";
    }
}
