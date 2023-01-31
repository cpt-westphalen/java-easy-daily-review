package application.repositories;

import java.util.List;

import application.entities.Review;

public interface ReviewRepository {
    public List<Review> getAll();

    public Review getById(String id);

    public List<Review> getManyByAuthorId(String authorId);

    public void add(Review review);

    public void removeById(String id);
}
