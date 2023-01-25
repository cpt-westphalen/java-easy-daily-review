package mocks;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import application.entities.Review;
import application.entities.TemplateReview.Period;
import application.repositories.ReviewRepository;

import config.Config;

public class InMemoryReviewRepository implements ReviewRepository {
    private Map<String, Review> reviews;

    private Path REVIEW_DB_PATH = Path.of(Config.MOCK_DB_PATH, "reviews.txt");

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
        writeToTextFile(review);

    }

    private void writeToTextFile(Review review) {
        try (BufferedWriter reviewDbWriter = new BufferedWriter(new FileWriter(REVIEW_DB_PATH.toFile(), true))) {
            reviewDbWriter.newLine();
            reviewDbWriter.write("----------");
            reviewDbWriter.write("id:" + review.getId());
            reviewDbWriter.write(review.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void removeById(String id) {
        this.reviews.remove(id);
    }

    @Override
    public List<Review> getManyByAuthorId(String authorId) {
        List<Review> reviewList = reviews.values().stream().filter(review -> review.getAuthorId().equals(authorId))
                .collect(Collectors.toList());
        return reviewList;
    }

}
