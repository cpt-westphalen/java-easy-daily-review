package application.repositories;

import java.util.List;

import application.entities.TemplateReview;

public interface TemplateReviewRepository {

    public TemplateReview findById(String id);

    public List<TemplateReview> listAll();

    public void add(TemplateReview templateReview);

    public void update(TemplateReview templateReview);
}
