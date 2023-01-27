package application.useCases;

import java.util.List;

import application.entities.TemplateQuestion;
import application.repositories.TemplateQuestionRepository;

public class GetTemplateQuestions {

    private TemplateQuestionRepository templateQuestionRepository;

    public GetTemplateQuestions(TemplateQuestionRepository templateQuestionRepository) {
        this.templateQuestionRepository = templateQuestionRepository;
    }

    public List<TemplateQuestion> exec() {
        List<TemplateQuestion> templateQuestions = this.templateQuestionRepository.getAll();
        return templateQuestions;
    }
}
