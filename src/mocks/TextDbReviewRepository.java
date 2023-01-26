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
import application.entities.TemplateReview.Period;
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
            Period period = null;
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
                        case "period":
                            if (value.equalsIgnoreCase("DAILY"))
                                period = Period.DAILY;
                            if (value.equalsIgnoreCase("WEEKLY"))
                                period = Period.WEEKLY;
                            break;
                        case "date":
                            date = LocalDate.parse(value);
                            break;
                    }
                }
                if (line.startsWith("questions")) {
                    String id = null, text = null, answerText = null;
                    Type type = null;
                    LocalDate updatedAt = null;
                    while ((line = reader.readLine()) != null) {
                        if (!line.startsWith("-")) {

                            String[] keyValuePair = line.split(": ");
                            String key = keyValuePair[0], value = keyValuePair[1];

                            switch (key.toLowerCase()) {
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

                            TemplateQuestion questionModel = new TemplateQuestion(id, type, text);
                            Answer answer = new Answer(type, answerText);
                            answer.setUpdatedAt(updatedAt);
                            Question question = new Question(questionModel, answer, updatedAt);
                            questions.add(question);
                        }
                    }
                }
            }
            Review review = new Review(authorId, reviewId, period, date, questions);
            Integer dayRate = review.getQuestionById(
                    "36276627-b507-41ff-b9f0-8bc7c9709986")
                    .getAnswer().getValueAsInteger();
            Integer wellbeingRate = review
                    .getQuestionById(
                            "1236d288-9b69-458e-8474-c58fcd35ad08")
                    .getAnswer().getValueAsInteger();
            Integer productivityRate = review
                    .getQuestionById(
                            "86f8f91a-17cb-4058-9dc2-5d439b3daa58")
                    .getAnswer().getValueAsInteger();

            review.setDayRate(dayRate);
            review.setWellbeingRate(wellbeingRate);
            review.setProductivityRate(productivityRate);
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
    public List<Review> getManyByPeriod(Period period) {
        List<Review> list = this.reviews.values().stream().filter(review -> review.getPeriod().equals(period))
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public void add(Review review) {
        List<Review> reviewListFromAuthor = getManyByAuthorId(review.getAuthorId());
        Review lastReview = reviewListFromAuthor.get(reviewListFromAuthor.size() - 1);
        if (lastReview.getDate().equals(review.getDate())) {
            removeById(lastReview.getId());
        }
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
            writer.write("period: " + review.getPeriod());
            writer.newLine();
            writer.write("date: " + review.getDate());
            writer.newLine();
            writer.write("questions:");
            List<Question> questions = review.getQuestions();
            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
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