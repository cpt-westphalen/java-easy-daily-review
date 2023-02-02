package application.useCases;

import java.util.List;
import java.util.stream.Collectors;

import application.entities.Rates;
import application.entities.Review;

public class GetRatesAverages {

    private List<Review> reviewsList;

    public GetRatesAverages(List<Review> reviewsList) {
        this.reviewsList = reviewsList;
    };

    public Rates exec() {

        Integer dayRateAvg = getDayRateAverage();
        Integer wellbeingRateAvg = getWellbeingRateAverage();
        Integer productivityRateAvg = getProductivityRateAverage();

        Rates rates = new Rates(productivityRateAvg, wellbeingRateAvg, dayRateAvg);

        return rates;

    }

    private Integer getDayRateAverage() {
        List<Review> reviewsWithDayRate = reviewsList.stream().filter(review -> review.getDayRate() != null)
                .collect(Collectors.toList());
        int total = reviewsWithDayRate.size();
        Integer dayRateAvg = 0;
        for (Review review : reviewsWithDayRate) {
            dayRateAvg += review.getDayRate();
        }
        dayRateAvg /= total;
        return dayRateAvg;
    }

    private Integer getWellbeingRateAverage() {
        List<Review> reviewsWithWellbeingRate = reviewsList.stream().filter(review -> review.getWellbeingRate() != null)
                .collect(Collectors.toList());
        int total = reviewsWithWellbeingRate.size();
        Integer wellbeingRateAvg = 0;
        for (Review review : reviewsWithWellbeingRate) {
            wellbeingRateAvg += review.getWellbeingRate();
        }
        wellbeingRateAvg /= total;
        return wellbeingRateAvg;
    }

    private Integer getProductivityRateAverage() {
        List<Review> reviewsWithProductivityRate = reviewsList.stream()
                .filter(review -> review.getProductivityRate() != null)
                .collect(Collectors.toList());
        int total = reviewsWithProductivityRate.size();
        Integer productivityRateAvg = 0;
        for (Review review : reviewsWithProductivityRate) {
            productivityRateAvg += review.getProductivityRate();
        }
        productivityRateAvg /= total;
        return productivityRateAvg;
    }

}
