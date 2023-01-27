package application.useCases;

import application.entities.TemplateReview;
import application.repositories.TemplateReviewRepository;

public class RemoveQuestionFromTemplateReview {
    private TemplateReviewRepository templateReviewRepository;

    public RemoveQuestionFromTemplateReview(TemplateReviewRepository templateReviewRepository) {
        this.templateReviewRepository = templateReviewRepository;
    }

    public void exec(String questionId, TemplateReview templateReview) throws Exception {
        if (this.templateReviewRepository.findById(templateReview.getId()) == null) {
            throw new Exception("Template Review (" + templateReview.getId() + ") is not registered on Database");
        }
        if (templateReview.getTemplateQuestionById(questionId) == null) {
            throw new Exception("Template Question (" + questionId + ") is not part of Template Review ("
                    + templateReview.getId() + ")");
        }
        templateReview.removeQuestion(questionId);
        this.templateReviewRepository.update(templateReview);
    }
}
