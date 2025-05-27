package gov.nysenate.ess.core.client.view.base;

import java.math.BigDecimal;

public class DonationInfoView implements ViewObject {
    protected BigDecimal maxDonation;
    protected BigDecimal accruedSickTime;

    public DonationInfoView(BigDecimal maxDonation, BigDecimal accruedSickTime) {
        this.maxDonation = maxDonation;
        this.accruedSickTime = accruedSickTime;
    }

    public BigDecimal getMaxDonation() {
        return maxDonation;
    }

    public BigDecimal getAccruedSickTime() {
        return accruedSickTime;
    }

    @Override
    public String getViewType() {
        return "donation-info-view";
    }
}
