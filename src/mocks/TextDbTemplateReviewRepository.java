package mocks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import application.entities.TemplateQuestion;
import application.entities.TemplateReview;

import application.repositories.TemplateReviewRepository;
import config.Config;
import utils.MakeTemplateQuestions;

public class TextDbTemplateReviewRepository implements TemplateReviewRepository {
    private Map<String, TemplateReview> templateReviews;

    private static final Path TEMPLATE_REVIEW_DB_PATH = Path.of(Config.MOCK_DB_PATH, "templates", "template-reviews");

    public TextDbTemplateReviewRepository() {
        this.templateReviews = new HashMap<String, TemplateReview>();

        // check text database for template files
        if (TEMPLATE_REVIEW_DB_PATH.toFile().exists()) {
            List<TemplateReview> templateReviewsFromTextFiles = listTemplateReviewsFromTextFiles(
                    TEMPLATE_REVIEW_DB_PATH);
            for (TemplateReview templateReview : templateReviewsFromTextFiles) {
                this.templateReviews.putIfAbsent(templateReview.getId(), templateReview);
            }
        } else {
            try {
                Files.createDirectory(TEMPLATE_REVIEW_DB_PATH);
            } catch (Exception e) {
                System.out.println("Error: could not create Template Review Database Folders");
            }
        }

    }

    @Override
    public TemplateReview findById(String id) {
        return templateReviews.get(id);
    }

    @Override
    public List<TemplateReview> listAll() {
        return templateReviews.values().stream().collect(Collectors.toList());
    }

    @Override
    public void add(TemplateReview templateReview) {
        templateReviews.putIfAbsent(templateReview.getId(), templateReview);
        writeTemplateReviewToTextFile(templateReview);
    }

    @Override
    public void update(TemplateReview templateReview) {
        templateReviews.replace(templateReview.getId(), templateReview);
        writeTemplateReviewToTextFile(templateReview);
    }

    public void writeTemplateReviewToTextFile(TemplateReview templateReview) {
        String filename = templateReview.getId() + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                Path.of(TEMPLATE_REVIEW_DB_PATH.toString(), filename).toFile()))) {
            writer.write("n " + templateReview.getDisplayName());
            writer.newLine();
            writer.write("i " + templateReview.getId());
            writer.newLine();
            for (TemplateQuestion templateQuestion : templateReview.getTemplateQuestions()) {
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
            }
        } catch (Exception e) {
            System.out.println("Error: could not write template review to file");
        }
    }

    public List<TemplateReview> listTemplateReviewsFromTextFiles(Path folderURI) {

        List<TemplateReview> templateReviews = new LinkedList<>();

        try (Stream<Path> files = Files.list(folderURI);) {

            Iterator<Path> fileIterator = files.iterator();

            while (fileIterator.hasNext()) {
                File file = fileIterator.next().toFile();

                TemplateReview templateReviewFromFile = makeTemplateReviewFromFile(file);

                templateReviews.add(templateReviewFromFile);
            }

            return templateReviews;

        } catch (Exception e) {
            System.out.println("Error: Unable to list template files from folder.");
            return null;
        }

    }

    private TemplateReview makeTemplateReviewFromFile(File file) {
        String id, name;

        List<TemplateQuestion> questions = new LinkedList<TemplateQuestion>();

        if (!file.exists())
            return null;

        try (Scanner fileScanner = new Scanner(file)) {

            String line = fileScanner.nextLine();
            // check for name
            if (line.startsWith("n")) {
                name = line.substring(2);
                line = fileScanner.nextLine();
            } else {
                name = null;
            }
            // check for id in template txt
            if (line.startsWith("i")) {
                id = line.substring(2);
                line = fileScanner.nextLine();
            } else {
                id = UUID.randomUUID().toString();
            }

            // create questions based on type / text from each line of the file
            questions = MakeTemplateQuestions.fromScannerNextLine(fileScanner);

            TemplateReview template = new TemplateReview(id, questions, name);

            return template;
        } catch (Exception e) {
            System.out.println("Could not read file at: " + file.getAbsolutePath());
            return null;
        }
    }

    @Override
    public void remove(TemplateReview templateReview) {
        templateReviews.remove(templateReview.getId());
        String filename = templateReview.getId() + ".txt";
        File file = Path.of(TEMPLATE_REVIEW_DB_PATH.toString(), filename).toFile();
        file.delete();
    }

}
