package application.useCases;

import application.entities.TemplateReview;
import application.repositories.TemplateReviewRepository;

public class UpdateTemplateReview {

    private TemplateReviewRepository templateReviewRepository;

    public UpdateTemplateReview(TemplateReviewRepository templateReviewRepository) {
        this.templateReviewRepository = templateReviewRepository;
    }

    public void exec(TemplateReview templateReview) {
        this.templateReviewRepository.update(templateReview);
    }
}
