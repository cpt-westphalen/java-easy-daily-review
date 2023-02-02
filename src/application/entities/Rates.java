package application.entities;

public class Rates {
    private Integer productivityRate;
    private Integer wellbeingRate;
    private Integer dayRate;

    public Rates(Integer productivityRate, Integer wellbeingRate, Integer dayRate) {
        this.productivityRate = productivityRate;
        this.wellbeingRate = wellbeingRate;
        this.dayRate = dayRate;
    }

    public Integer getProductivityRate() {
        return productivityRate;
    }

    public void setProductivityRate(Integer productivityRate) {
        this.productivityRate = productivityRate;
    }

    public Integer getWellbeingRate() {
        return wellbeingRate;
    }

    public void setWellbeingRate(Integer wellbeingRate) {
        this.wellbeingRate = wellbeingRate;
    }

    public Integer getDayRate() {
        return dayRate;
    }

    public void setDayRate(Integer dayRate) {
        this.dayRate = dayRate;
    }

}
