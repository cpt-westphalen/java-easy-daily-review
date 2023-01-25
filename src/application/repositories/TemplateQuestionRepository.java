package application.repositories;

import java.util.List;

import application.entities.TemplateQuestion;

public interface TemplateQuestionRepository {

    public List<TemplateQuestion> getAll();

    public TemplateQuestion findById(String id);

    public boolean update(String id, TemplateQuestion newTemplateQuestion);

    public void add(TemplateQuestion templateQuestion);

    public void remove(TemplateQuestion templateQuestion);
}
