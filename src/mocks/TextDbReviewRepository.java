package mocks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import application.entities.Answer;
import application.entities.Question;
import application.entities.Review;
import application.entities.TemplateQuestion;
import application.entities.TemplateQuestion.Type;

import application.repositories.ReviewRepository;

import config.Config;

public class TextDbReviewRepository implements ReviewRepository {
    private Map<String, Review> reviews;

    private Path REVIEW_DB_PATH = Path.of(Config.MOCK_DB_PATH, "reviews");

    public TextDbReviewRepository() {
        this.reviews = new HashMap<String, Review>();
        List<Review> dbReviews = readReviewsFromFiles(REVIEW_DB_PATH);
        if (dbReviews != null) {
            for (Review review : dbReviews) {
                this.reviews.put(review.getId(), review);
            }
        }
    }

    private List<Review> readReviewsFromFiles(Path userFoldersPath) {
        try (Stream<Path> folders = Files.list(userFoldersPath)) {
            List<Review> reviews = new LinkedList<Review>();
            Review review = null;
            Iterator<Path> foldersIterator = folders.iterator();
            while (foldersIterator.hasNext()) {
                Path folder = foldersIterator.next();
                try (Stream<Path> files = Files.list(folder)) {

                    Iterator<Path> filesIterator = files.iterator();
                    while (filesIterator.hasNext()) {
                        Path filePath = filesIterator.next();
                        review = makeReviewFromFile(filePath);
                        if (review != null)
                            reviews.add(review);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
            return reviews;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Review makeReviewFromFile(Path filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            String authorId = null, reviewId = null;
            LocalDate date = null;
            List<Question> questions = new LinkedList<Question>();
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("questions") && !line.startsWith("-")) {
                    String[] keyValuePair = line.split(": ");
                    String key = keyValuePair[0], value = keyValuePair[1];
                    switch (key.toLowerCase()) {
                        case "authorid":
                            authorId = value;
                            break;
                        case "reviewid":
                            reviewId = value;
                            break;
                        case "date":
                            date = LocalDate.parse(value);
                            break;
                    }
                }
                if (line.startsWith("questions")) {
                    String id = null, text = null, answerText = null, displayName = null;
                    Type type = null;
                    LocalDate updatedAt = null;
                    while ((line = reader.readLine()) != null) {
                        if (!line.startsWith("-")) {

                            String[] keyValuePair = line.split(": ");
                            String key = keyValuePair[0], value = keyValuePair[1];

                            switch (key.toLowerCase()) {
                                case "name":
                                    displayName = value;
                                case "id":
                                    id = value;
                                    break;
                                case "type":
                                    if (value.equalsIgnoreCase("TEXT"))
                                        type = Type.TEXT;
                                    if (value.equalsIgnoreCase("NUMBER"))
                                        type = Type.NUMBER;
                                    if (value.equalsIgnoreCase("BOOLEAN"))
                                        type = Type.BOOLEAN;
                                    break;
                                case "q":
                                    text = value;
                                    break;
                                case "r":
                                    answerText = value;
                                    break;
                                case "updated at":
                                    updatedAt = LocalDate.parse(value);
                                    break;
                                default:
                                    break;
                            }
                        } else {

                            TemplateQuestion questionModel = new TemplateQuestion(id, type, text, displayName);
                            Answer answer = new Answer(type, answerText);
                            answer.setUpdatedAt(updatedAt);
                            Question question = new Question(questionModel, answer, updatedAt);
                            questions.add(question);
                        }
                    }
                }
            }
            Review review = new Review(authorId, reviewId, date, questions);

            return review;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<Review> getAll() {
        List<Review> reviewsList = this.reviews.values().stream().collect(Collectors.toList());
        return reviewsList;
    }

    @Override
    public Review getById(String id) {
        return this.reviews.get(id);
    }

    @Override
    public void add(Review review) {
        getManyByAuthorId(review.getAuthorId()).stream().forEach(r -> {
            if (r.getDate().equals(review.getDate())) {
                removeById(r.getId());
            }
        });
        this.reviews.put(review.getId(), review);
        writeToTextFile(review);
    }

    private void writeToTextFile(Review review) {
        Path userFolder = Path.of(REVIEW_DB_PATH.toString(),
                review.getAuthorId());
        try {
            Files.createDirectories(userFolder);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                (new File(userFolder.toString(),
                        review.getDate().toString() + ".txt"))))) {
            writer.write("authorId: " + review.getAuthorId());
            writer.newLine();
            writer.write("reviewId: " + review.getId());
            writer.newLine();
            writer.write("date: " + review.getDate());
            writer.newLine();
            writer.write("questions:");
            List<Question> questions = review.getQuestions();
            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
                writer.newLine();
                writer.write("name: " + question.getDisplayName());
                writer.newLine();
                writer.write("id: " + question.getId());
                writer.newLine();
                writer.write("type: " + question.getType().name());
                writer.newLine();
                writer.write("Q: " + question.getText());
                writer.newLine();
                writer.write("R: " + question.getAnswer().getValue());
                writer.newLine();
                writer.write("Updated at: " + question.getUpdatedAt().toString());
                writer.newLine();
                writer.write("-----");
            }
            writer.newLine();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void removeById(String id) {
        this.reviews.remove(id);
    }

    @Override
    public List<Review> getManyByAuthorId(String authorId) {
        List<Review> reviewList = reviews.values().stream().filter(review -> review.getAuthorId().equals(authorId))
                .collect(Collectors.toList());
        return reviewList;
    }

}
