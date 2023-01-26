package application.useCases;

import java.time.LocalDate;
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

    public boolean hasReviewedToday() {
        String authorId = Auth.getLoggedUser().getId();
        List<Review> reviewsList = reviewRepository.getManyByAuthorId(authorId);
        if (reviewsList != null && reviewsList.size() > 0) {
            Review lastReview = reviewsList.get(reviewsList.size() - 1);
            return lastReview.getDate().equals(LocalDate.now());
        }
        return false;
    }
}
