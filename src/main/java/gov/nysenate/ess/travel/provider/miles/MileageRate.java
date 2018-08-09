package gov.nysenate.ess.travel.provider.miles;

import java.time.LocalDate;

public class MileageRate {

    private LocalDate startDate;
    private LocalDate endDate;
    private String rate;

    public MileageRate(LocalDate startDate, LocalDate endDate, String rate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.rate = rate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
