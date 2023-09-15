package gov.nysenate.ess.time.model.accrual;

import java.math.BigDecimal;

public class PeriodDonation {
    private final BigDecimal donatedYtd, periodDonation;

    public PeriodDonation(BigDecimal donatedYtd, BigDecimal periodDonation) {
        this.donatedYtd = donatedYtd;
        this.periodDonation = periodDonation;
    }

    public BigDecimal getDonatedYtd() {
        return donatedYtd;
    }

    public BigDecimal getPeriodDonation() {
        return periodDonation;
    }
}
