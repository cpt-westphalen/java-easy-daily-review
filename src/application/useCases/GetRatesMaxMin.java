package application.useCases;

import java.util.LinkedList;
import java.util.List;

import application.entities.Rates;
import application.entities.Review;

public class GetRatesMaxMin {
    private List<Review> reviewsList;
    private List<Review> reviewsWithDayRate;
    private List<Review> reviewsWithProductivityRate;
    private List<Review> reviewsWithWellbeingRate;

    public GetRatesMaxMin(List<Review> reviewsList) {
        this.reviewsList = reviewsList;

        reviewsWithDayRate = new LinkedList<>();
        reviewsWithProductivityRate = new LinkedList<>();
        reviewsWithWellbeingRate = new LinkedList<>();

        for (Review review : this.reviewsList) {
            if (review.getDayRate() != null)
                reviewsWithDayRate.add(review);
            if (review.getProductivityRate() != null)
                reviewsWithProductivityRate.add(review);
            if (review.getWellbeingRate() != null)
                reviewsWithWellbeingRate.add(review);
        }
    }

    public Rates minRates() {
        Integer minDayRate = getMinDayRate();
        Integer minProductivityRate = getMinProductivityRate();
        Integer minWellbeingRate = getMinWellbeingRate();
        Rates minRates = new Rates(minProductivityRate, minWellbeingRate, minDayRate);
        return minRates;
    }

    public Rates maxRates() {
        Integer maxDayRate = getMaxDayRate();
        Integer maxProductivityRate = getMaxProductivityRate();
        Integer maxWellbeingRate = getMaxWellbeingRate();
        Rates maxRates = new Rates(maxProductivityRate, maxWellbeingRate, maxDayRate);
        return maxRates;
    }

    private Integer getMinDayRate() {
        Integer min;
        min = reviewsWithDayRate.get(0).getDayRate();
        for (Review review : reviewsWithDayRate) {
            Integer dayRate = review.getDayRate();
            if (dayRate < min)
                min = dayRate;
        }
        return min;
    }

    private Integer getMinProductivityRate() {
        Integer min;
        min = reviewsWithProductivityRate.get(0).getProductivityRate();
        for (Review review : reviewsWithProductivityRate) {
            Integer productivityRate = review.getProductivityRate();
            if (productivityRate < min)
                min = productivityRate;
        }
        return min;
    }

    private Integer getMinWellbeingRate() {
        Integer min;
        min = reviewsWithWellbeingRate.get(0).getWellbeingRate();
        for (Review review : reviewsWithWellbeingRate) {
            Integer wellbeingRate = review.getWellbeingRate();
            if (wellbeingRate < min)
                min = wellbeingRate;
        }
        return min;
    }

    private Integer getMaxDayRate() {
        Integer max;
        max = reviewsWithDayRate.get(0).getDayRate();
        for (Review review : reviewsWithDayRate) {
            Integer dayRate = review.getDayRate();
            if (dayRate > max)
                max = dayRate;
        }
        return max;
    }

    private Integer getMaxProductivityRate() {
        Integer max;
        max = reviewsWithProductivityRate.get(0).getProductivityRate();
        for (Review review : reviewsWithProductivityRate) {
            Integer productivityRate = review.getProductivityRate();
            if (productivityRate > max)
                max = productivityRate;
        }
        return max;
    }

    private Integer getMaxWellbeingRate() {
        Integer max;
        max = reviewsWithWellbeingRate.get(0).getWellbeingRate();
        for (Review review : reviewsWithWellbeingRate) {
            Integer wellbeingRate = review.getWellbeingRate();
            if (wellbeingRate > max)
                max = wellbeingRate;
        }
        return max;
    }
}