package application.useCases;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import application.entities.Question;
import application.entities.Review;
import application.entities.Template;
import application.entities.Question.Type;
import application.entities.Template.Period;
import application.repositories.ReviewRepository;

public class RegisterNewReview {
    private ReviewRepository reviewRepository;

    public RegisterNewReview(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review getTemplateDailyReview() {
        String id = UUID.randomUUID().toString();
        LocalDateTime date = LocalDateTime.now();
        Period period = Period.DAILY;

        List<Question> questions = new LinkedList<Question>();

        String templateFolderPath = new File("src/templates")
                .getAbsolutePath();
        Path path = Path.of(templateFolderPath, "daily-review-template.txt");
        Scanner templateScanner = null;
        try {
            templateScanner = new Scanner(path);
        } catch (Exception e) {
            return null;
        }
        // check for periodicity
        String line = templateScanner.nextLine();
        if (line.equalsIgnoreCase("daily")) {
            period = Period.DAILY;
        } else if (line.equalsIgnoreCase("weekly")) {
            period = Period.WEEKLY;
        } else if (line.equalsIgnoreCase("quarterly")) {
            period = Period.QUARTERLY;
        } else if (line.equalsIgnoreCase("yearly")) {
            period = Period.YEARLY;
        }
        // create questions based on type / text from each line of the file
        while (templateScanner.hasNextLine()) {
            Question question;
            String questionId = UUID.randomUUID().toString();
            Type questionType;
            String text;
            // read from template file
            line = templateScanner.nextLine();
            switch (line.charAt(0)) {
                case '&':
                    questionType = Type.TEXT;
                    break;
                case '!':
                    questionType = Type.BOOLEAN;
                    break;
                case '#':
                    questionType = Type.NUMBER;
                    break;
                default:
                    questionType = Type.TEXT;
            }
            text = line.substring(2);
            question = new Question(questionId, questionType, text);
            questions.add(question);
        }
        templateScanner.close();

        // last question is always an overall rating;
        Question lastQuestion = new Question(UUID.randomUUID().toString(), Type.NUMBER, Template.lastQuestionText);
        questions.add(lastQuestion);

        Review template = new Review(id, period, date, questions);

        return template;
    }

    public void saveToRepository(Review review) {
        this.reviewRepository.add(review);
    }
}
