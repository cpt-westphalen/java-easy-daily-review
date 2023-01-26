package application.useCases;

import java.util.List;

import application.entities.TemplateReview;
import application.repositories.TemplateReviewRepository;

public class ListTemplateReviews {
    private TemplateReviewRepository templateReviewRepository;

    public ListTemplateReviews(TemplateReviewRepository templateReviewRepository) {
        this.templateReviewRepository = templateReviewRepository;
    }

    public List<TemplateReview> exec() {
        return this.templateReviewRepository.listAll();
    }

}
