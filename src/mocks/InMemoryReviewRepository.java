package mocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import application.entities.Review;
import application.entities.Template.Period;
import application.repositories.ReviewRepository;

public class InMemoryReviewRepository implements ReviewRepository {
    private Map<String, Review> reviews;

    public InMemoryReviewRepository() {
        this.reviews = new HashMap<String, Review>();
    }

    @Override
    public List<Review> getAll() {
        List<Review> reviewsList = this.reviews.values().stream().collect(Collectors.toList());
        return reviewsList;
    }

    @Override
    public Review getById(String id) {
        return this.reviews.get(id);
    }

    @Override
    public List<Review> getManyByPeriod(Period period) {
        List<Review> list = this.reviews.values().stream().filter(review -> review.getPeriod().equals(period))
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public void add(Review review) {
        this.reviews.put(review.getId(), review);
    }

    @Override
    public void removeById(String id) {
        this.reviews.remove(id);
    }

}
