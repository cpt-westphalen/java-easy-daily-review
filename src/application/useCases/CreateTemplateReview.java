package application.useCases;

import java.util.List;
import java.util.UUID;

import application.entities.TemplateQuestion;
import application.entities.TemplateReview;

import application.repositories.TemplateReviewRepository;

public class CreateTemplateReview {
    private TemplateReviewRepository templateReviewRepository;

    public CreateTemplateReview(TemplateReviewRepository templateReviewRepository) {
        this.templateReviewRepository = templateReviewRepository;
    }

    public TemplateReview exec(String name, List<TemplateQuestion> templateQuestionsList)
            throws Exception {
        if (templateQuestionsList == null) {
            throw new Exception("A Template Review's question list can't be null.");
        }

        String id = UUID.randomUUID().toString();
        TemplateReview customTemplateReview = new TemplateReview(id, templateQuestionsList, name);

        this.templateReviewRepository.add(customTemplateReview);

        return customTemplateReview;
    }
}
