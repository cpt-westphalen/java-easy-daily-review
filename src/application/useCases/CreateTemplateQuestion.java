package application.useCases;

import java.util.UUID;

import application.entities.TemplateQuestion;
import application.entities.TemplateQuestion.Type;
import application.repositories.TemplateQuestionRepository;

public class CreateTemplateQuestion {

    private TemplateQuestionRepository templateQuestionRepository;

    public CreateTemplateQuestion(TemplateQuestionRepository templateQuestionRepository) {
        this.templateQuestionRepository = templateQuestionRepository;
    }

    public TemplateQuestion exec(String id, Type type, String questionText) throws Exception {
        if (type == null || questionText == null || questionText.isEmpty()) {
            throw new Exception("Template question must include a valid type and text.");
        }

        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        TemplateQuestion newTemplateQuestion = new TemplateQuestion(id, type, questionText);

        addOrUpdateTemplateQuestionToRepository(newTemplateQuestion);

        return newTemplateQuestion;
    }

    private void addOrUpdateTemplateQuestionToRepository(TemplateQuestion templateQuestion) {
        TemplateQuestion existingTemplateQuestion = templateQuestionRepository.findById(templateQuestion.getId());
        boolean isIdTaken = existingTemplateQuestion != null;
        if (isIdTaken) {
            if (!templateQuestion.getText().equalsIgnoreCase(existingTemplateQuestion.getText())) {
                existingTemplateQuestion.setText(templateQuestion.getText());
            }
            if (!templateQuestion.getType().equals(existingTemplateQuestion.getType())) {
                existingTemplateQuestion.setType(templateQuestion.getType());
            }
            templateQuestionRepository.update(existingTemplateQuestion.getId(), existingTemplateQuestion);
            return;
        }
        templateQuestionRepository.add(templateQuestion);
    }

}
