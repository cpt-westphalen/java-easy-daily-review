package application.useCases;

import java.util.List;
import java.util.UUID;

import application.entities.TemplateQuestion;
import application.entities.TemplateReview;
import application.entities.TemplateReview.Period;
import application.repositories.TemplateReviewRepository;

public class CreateTemplateReview {
    private TemplateReviewRepository templateReviewRepository;

    public CreateTemplateReview(TemplateReviewRepository templateReviewRepository) {
        this.templateReviewRepository = templateReviewRepository;
    }

    public TemplateReview exec(String name, Period period, List<TemplateQuestion> templateQuestionsList)
            throws Exception {
        if (period == null) {
            throw new Exception("A Template Review's periodicity can't be null.");
        }
        if (templateQuestionsList == null) {
            throw new Exception("A Template Review's question list can't be null.");
        }

        String id = UUID.randomUUID().toString();
        TemplateReview customTemplateReview = new TemplateReview(id, period, templateQuestionsList, name);

        this.templateReviewRepository.add(customTemplateReview);

        return customTemplateReview;
    }
}
