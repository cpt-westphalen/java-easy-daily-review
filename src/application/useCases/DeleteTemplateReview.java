package application.useCases;

import application.entities.TemplateReview;
import application.repositories.TemplateReviewRepository;

public class DeleteTemplateReview {

    private TemplateReviewRepository templateReviewRepository;

    public DeleteTemplateReview(TemplateReviewRepository templateReviewRepository) {
        this.templateReviewRepository = templateReviewRepository;
    }

    public void exec(TemplateReview templateReview) throws Exception {
        if (templateReview == null) {
            throw new Exception("Must select a valid Template Review for deletion");
        }
        templateReviewRepository.remove(templateReview);
    }

}
