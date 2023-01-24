package application.useCases;

import java.util.List;

import application.Auth;
import application.entities.Review;
import application.repositories.ReviewRepository;

public class GetReviews {
    private ReviewRepository reviewRepository;

    public GetReviews(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> listAllFromLoggedUser() throws Exception {
        String authorId = Auth.getLoggedUser().getId();
        return reviewRepository.getManyByAuthorId(authorId);
    }
}
