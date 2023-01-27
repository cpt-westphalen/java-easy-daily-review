package mocks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import application.entities.TemplateQuestion;
import application.entities.TemplateQuestion.Type;
import application.repositories.TemplateQuestionRepository;
import config.Config;

public class TextDbTemplateQuestionRepository implements TemplateQuestionRepository {

    private Map<String, TemplateQuestion> templateQuestions;

    private static final Path TEMPLATE_QUESTION_DB_PATH = Path.of(Config.MOCK_DB_PATH, "templates",
            "template-questions");

    public TextDbTemplateQuestionRepository() {
        this.templateQuestions = new HashMap<String, TemplateQuestion>();

        // check text database for template files
        if (TEMPLATE_QUESTION_DB_PATH.toFile().exists()) {
            List<TemplateQuestion> templateQuestionsFromTextFile = listTemplateQuestionsFromTextFilesFolderPath(
                    TEMPLATE_QUESTION_DB_PATH);
            for (TemplateQuestion templateQuestion : templateQuestionsFromTextFile) {
                this.templateQuestions.putIfAbsent(templateQuestion.getId(), templateQuestion);
            }
        } else {
            try {
                Files.createDirectory(TEMPLATE_QUESTION_DB_PATH);
            } catch (Exception e) {
                System.out.println("Error: could not create Template Review Database Folders");
            }
        }
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
        writeTemplateQuestionToTextFile(newTemplateQuestion);
        return true;
    }

    @Override
    public void add(TemplateQuestion templateQuestion) {
        this.templateQuestions.put(templateQuestion.getId(), templateQuestion);
        writeTemplateQuestionToTextFile(templateQuestion);

    }

    public void writeTemplateQuestionToTextFile(TemplateQuestion templateQuestion) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                Path.of(TEMPLATE_QUESTION_DB_PATH.toString(), templateQuestion.getId() + ".txt").toFile()))) {
            writer.write("n " + templateQuestion.getDisplayName());
            writer.newLine();
            writer.write("i " + templateQuestion.getId());
            writer.newLine();
            String typeChar;
            switch (templateQuestion.getType().name()) {
                case "TEXT":
                    typeChar = "&";
                    break;
                case "BOOLEAN":
                    typeChar = "!";
                    break;
                case "NUMBER":
                    typeChar = "#";
                    break;

                default:
                    typeChar = "&";
                    break;
            }
            writer.write(typeChar + " " + templateQuestion.getText());
            writer.newLine();
        } catch (Exception e) {
            System.out.println("Error: could not write template question to file");
        }
    }

    @Override
    public void remove(TemplateQuestion templateQuestion) {
        this.templateQuestions.remove(templateQuestion.getId());

    }

    public List<TemplateQuestion> listTemplateQuestionsFromTextFilesFolderPath(Path folderURI) {

        List<TemplateQuestion> templateQuestions = new LinkedList<>();

        try (Stream<Path> files = Files.list(folderURI);) {

            Iterator<Path> fileIterator = files.iterator();

            while (fileIterator.hasNext()) {
                File file = fileIterator.next().toFile();

                TemplateQuestion templateReviewFromFile = makeTemplateQuestionFromFile(file);

                templateQuestions.add(templateReviewFromFile);
            }

            return templateQuestions;

        } catch (Exception e) {
            System.out.println("Error: Unable to list template question files from folder.");
            return null;
        }

    }

    public TemplateQuestion makeTemplateQuestionFromFile(File file) {
        String templateQuestionId, templateQuestionText, templateQuestionDisplayName = null;
        Type templateQuestionType;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("n")) {
                    templateQuestionDisplayName = line.substring(2);
                    line = reader.readLine();
                }
                if (line.startsWith("i")) {
                    templateQuestionId = line.substring(2);
                    line = reader.readLine();
                } else {
                    templateQuestionId = UUID.randomUUID().toString();
                }
                switch (line.charAt(0)) {
                    case '&':
                        templateQuestionType = Type.TEXT;
                        break;
                    case '!':
                        templateQuestionType = Type.BOOLEAN;
                        break;
                    case '#':
                        templateQuestionType = Type.NUMBER;
                        break;
                    default:
                        templateQuestionType = Type.TEXT;
                }
                templateQuestionText = line.substring(2);
                TemplateQuestion templateQuestion = new TemplateQuestion(templateQuestionId, templateQuestionType,
                        templateQuestionText, templateQuestionDisplayName);
                return templateQuestion;
            }
        } catch (Exception e) {
            System.out.println("Error: Unable to make question from file: " + file.getAbsolutePath());
        }
        return null;
    }

}
