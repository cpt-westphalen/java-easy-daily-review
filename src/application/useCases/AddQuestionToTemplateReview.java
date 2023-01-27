package application.useCases;

import application.entities.TemplateQuestion;
import application.entities.TemplateReview;
import application.repositories.TemplateReviewRepository;

public class AddQuestionToTemplateReview {

    private TemplateReviewRepository templateReviewRepository;

    public AddQuestionToTemplateReview(TemplateReviewRepository templateReviewRepository) {
        this.templateReviewRepository = templateReviewRepository;
    }

    public TemplateReview exec(TemplateReview templateReview, TemplateQuestion templateQuestion) {
        boolean hasTemplateQuestionWithId = templateReview.getTemplateQuestionById(templateQuestion.getId()) != null;
        if (hasTemplateQuestionWithId) {
            return null;
        }
        templateReview.addQuestion(templateQuestion);
        this.templateReviewRepository.update(templateReview);
        return templateReview;
    }

}
