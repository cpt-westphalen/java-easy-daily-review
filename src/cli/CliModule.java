package cli;

import application.repositories.ReviewRepository;
import application.repositories.UserRepository;
import mocks.InMemoryReviewRepository;
import mocks.InMemoryUserRepository;

public class CliModule {
    public static UserRepository userRepository = new InMemoryUserRepository();
    public static ReviewRepository reviewRepository = new InMemoryReviewRepository();
}
