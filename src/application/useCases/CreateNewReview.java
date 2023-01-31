package application.useCases;

import java.time.LocalDate;
import java.util.List;

import application.entities.Question;
import application.entities.Review;

import application.repositories.ReviewRepository;

public class CreateNewReview {
        private ReviewRepository reviewRepository;

        public CreateNewReview(ReviewRepository reviewRepository) {
                this.reviewRepository = reviewRepository;
        }

        public Review exec(String authorId, String reviewId, LocalDate date, List<Question> questions) {
                // create the new review from the questions and answers
                Review review = new Review(authorId, reviewId, date, questions);

                // set default rates by querying the question id
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

                this.reviewRepository.add(review);

                return review;
        }

}
