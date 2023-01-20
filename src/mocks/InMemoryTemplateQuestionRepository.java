package mocks;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import application.entities.TemplateQuestion;
import application.repositories.TemplateQuestionRepository;
import utils.MakeTemplateQuestions;

public class InMemoryTemplateQuestionRepository implements TemplateQuestionRepository {

    private Map<String, TemplateQuestion> templateQuestions;
    private TemplateQuestion productivityRateQuestion;
    private TemplateQuestion wellbeingRateQuestion;
    private TemplateQuestion dayRateQuestion;

    public InMemoryTemplateQuestionRepository() {
        this.templateQuestions = new HashMap<String, TemplateQuestion>();
        List<TemplateQuestion> defaultTemplateQuestions = readTemplateQuestionsFile();
        if (defaultTemplateQuestions != null) {
            for (TemplateQuestion question : defaultTemplateQuestions) {
                this.templateQuestions.put(question.getId(), question);
            }
        }
    }

    private List<TemplateQuestion> readTemplateQuestionsFile() {
        List<TemplateQuestion> templateQuestionsList = new LinkedList<TemplateQuestion>();

        String templateFolderPath = new File("src/templates")
                .getAbsolutePath();
        Path path = Path.of(templateFolderPath, "default-template-questions.txt");
        Scanner templateScanner = null;
        try {
            templateScanner = new Scanner(path);
        } catch (Exception e) {
            return null;
        }
        templateQuestionsList = MakeTemplateQuestions.fromScannerNextLine(templateScanner);
        this.productivityRateQuestion = templateQuestionsList.get(0);
        this.wellbeingRateQuestion = templateQuestionsList.get(1);
        this.dayRateQuestion = templateQuestionsList.get(2);
        return templateQuestionsList;
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

    @Override
    public TemplateQuestion getDayRateQuestion() {
        return this.dayRateQuestion;
    }

    @Override
    public TemplateQuestion getWellbeingRateQuestion() {
        return this.wellbeingRateQuestion;
    }

    @Override
    public TemplateQuestion getProductivityRateQuestion() {
        return this.productivityRateQuestion;
    }

}
