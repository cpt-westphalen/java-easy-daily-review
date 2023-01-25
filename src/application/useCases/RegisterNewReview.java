package application.useCases;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import application.entities.Review;
import application.entities.TemplateQuestion;
import application.entities.TemplateReview;
import application.entities.TemplateReview.Period;
import application.repositories.ReviewRepository;
import utils.MakeTemplateQuestions;

public class RegisterNewReview {
    private ReviewRepository reviewRepository;

    public RegisterNewReview(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public TemplateReview getTemplateReviewFrom(String folderURI, String filename) {
        String id;
        Period period = Period.DAILY;

        List<TemplateQuestion> questions = new LinkedList<TemplateQuestion>();

        String templateFolderPath = new File(folderURI)
                .getAbsolutePath();
        Path path = Path.of(templateFolderPath, filename);
        Scanner templateScanner = null;
        try {
            templateScanner = new Scanner(path);
        } catch (Exception e) {
            return null;
        }
        // check for id in template txt
        String line = templateScanner.nextLine();
        if (line.startsWith("i")) {
            id = line.substring(2);
            line = templateScanner.nextLine();
        } else {
            id = UUID.randomUUID().toString();
        }
        // check for periodicity
        if (line.equalsIgnoreCase("daily")) {
            period = Period.DAILY;
        } else if (line.equalsIgnoreCase("weekly")) {
            period = Period.WEEKLY;
        }
        // create questions based on type / text from each line of the file
        questions = MakeTemplateQuestions.fromScannerNextLine(templateScanner);

        TemplateReview template = new TemplateReview(id, period, questions);

        return template;
    }

    public void saveToRepository(Review review) {
        this.reviewRepository.add(review);
    }
}
