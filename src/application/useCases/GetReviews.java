package application.useCases;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Review> listRecentFromLoggedUser(int minusDays) throws Exception {
        String authorId = Auth.getLoggedUser().getId();
        List<Review> allReviews = reviewRepository.getManyByAuthorId(authorId);
        List<Review> recentReviews = allReviews.stream()
                .filter(review -> review.getDate().isAfter(LocalDate.now().minusDays(minusDays)))
                .collect(Collectors.toList());
        recentReviews.sort((a, b) -> a.getDate().isAfter(b.getDate()) ? 1 : -1);
        return recentReviews;
    }

    public List<Review> withQuestion(String questionId) {
        String authorId = Auth.getLoggedUser().getId();
        List<Review> allReviews = this.reviewRepository.getManyByAuthorId(authorId);
        List<Review> reviewsWithSelectedQuestion = allReviews.stream()
                .filter(review -> review.getQuestionById(questionId) != null).collect(Collectors.toList());
        return reviewsWithSelectedQuestion;
    }

    public boolean hasReviewedToday() {
        String authorId = Auth.getLoggedUser().getId();
        List<Review> reviewsList = reviewRepository.getManyByAuthorId(authorId);
        if (reviewsList != null && reviewsList.size() > 0) {
            for (Review review : reviewsList) {
                if (review.getDate().equals(LocalDate.now())) {
                    return true;
                }
            }
        }
        return false;
    }
}
