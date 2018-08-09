package gov.nysenate.ess.travel.provider.miles;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MileageRate {

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal rate;

    public MileageRate(LocalDate startDate, LocalDate endDate, String rate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.rate = new BigDecimal(rate);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
