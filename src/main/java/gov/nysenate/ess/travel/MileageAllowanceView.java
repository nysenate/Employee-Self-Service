package gov.nysenate.ess.travel;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.route.LegView;

import java.math.BigDecimal;

public class MileageAllowanceView implements ViewObject {

    LegView leg;
    String miles;
    String mileageRate;
    String allowance;

    public MileageAllowanceView() {
    }

    public MileageAllowanceView(MileageAllowance mileageAllowance) {
        this.leg = new LegView(mileageAllowance.getLeg());
        this.miles = String.valueOf(mileageAllowance.getMiles());
        this.mileageRate = mileageAllowance.getMileageRate().toString();
        this.allowance = mileageAllowance.allowance().toString();
    }

    public MileageAllowance toLegMileageAllowance() {
        return new MileageAllowance(leg.toLeg(), Double.valueOf(miles), new BigDecimal(mileageRate));
    }

    public LegView getLeg() {
        return leg;
    }

    public String getMiles() {
        return miles;
    }

    public String getMileageRate() {
        return mileageRate;
    }

    public String getAllowance() {
        return allowance;
    }

    @Override
    public String getViewType() {
        return "leg-mileage-allowance";
    }
}
