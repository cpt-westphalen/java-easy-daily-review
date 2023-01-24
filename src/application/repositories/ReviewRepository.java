package application.repositories;

import java.util.List;

import application.entities.Review;
import application.entities.TemplateReview.Period;

public interface ReviewRepository {
    public List<Review> getAll();

    public Review getById(String id);

    public List<Review> getManyByAuthorId(String authorId);

    public List<Review> getManyByPeriod(Period period);

    public void add(Review review);

    public void removeById(String id);
}
