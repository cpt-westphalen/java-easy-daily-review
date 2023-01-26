package cli;

import application.repositories.ReviewRepository;
import application.repositories.TemplateQuestionRepository;
import application.repositories.TemplateReviewRepository;
import application.repositories.UserRepository;
import mocks.TextDbReviewRepository;
import mocks.InMemoryTemplateQuestionRepository;
import mocks.TextDbUserRepository;
import mocks.TextDbTemplateReviewRepository;

public class CliModule {
    public static UserRepository userRepository = new TextDbUserRepository();
    public static ReviewRepository reviewRepository = new TextDbReviewRepository();
    public static TemplateQuestionRepository templateQuestionRepository = new InMemoryTemplateQuestionRepository();
    public static TemplateReviewRepository templateReviewRepository = new TextDbTemplateReviewRepository();
}
