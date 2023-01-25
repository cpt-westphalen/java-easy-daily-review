package mocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import application.entities.TemplateQuestion;
import application.repositories.TemplateQuestionRepository;

public class InMemoryTemplateQuestionRepository implements TemplateQuestionRepository {

    private Map<String, TemplateQuestion> templateQuestions;

    public InMemoryTemplateQuestionRepository() {
        this.templateQuestions = new HashMap<String, TemplateQuestion>();
    }

    @Override
    public List<TemplateQuestion> getAll() {
        return this.templateQuestions.values().stream().collect(Collectors.toList());
    }

    @Override
    public TemplateQuestion findById(String id) {
        return this.templateQuestions.get(id);
    }

    @Override
    public boolean update(String id, TemplateQuestion newTemplateQuestion) {
        TemplateQuestion tq = this.templateQuestions.get(id);
        if (tq == null) {
            return false;
        }
        this.templateQuestions.replace(id, tq);
        return true;
    }

    @Override
    public void add(TemplateQuestion templateQuestion) {
        this.templateQuestions.put(templateQuestion.getId(), templateQuestion);

    }

    @Override
    public void remove(TemplateQuestion templateQuestion) {
        this.templateQuestions.remove(templateQuestion.getId());

    }

}
