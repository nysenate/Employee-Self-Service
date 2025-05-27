package gov.nysenate.ess.travel.provider.miles;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MileageRateView implements ViewObject {

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal rate;

    public MileageRateView(MileageRate mileageRate) {
        this.startDate = mileageRate.startDate();
        this.endDate = mileageRate.endDate();
        this.rate = mileageRate.rate();
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

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public String getViewType() {
        return "Mileage-Rate-View";
    }
}
