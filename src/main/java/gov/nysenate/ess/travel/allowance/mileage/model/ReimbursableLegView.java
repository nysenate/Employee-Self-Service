package gov.nysenate.ess.travel.allowance.mileage.model;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.math.BigDecimal;

public class ReimbursableLegView implements ViewObject {

    private LegView leg;
    private String distance;

    public ReimbursableLegView() {
    }

    public ReimbursableLegView(ReimbursableLeg leg) {
        this.leg = new LegView(leg.getLeg());
        this.distance = leg.getDistance().toString();
    }

    public ReimbursableLeg toReimbursableLeg() {
        return new ReimbursableLeg(leg.toLeg(), new BigDecimal(distance));
    }

    public LegView getLeg() {
        return leg;
    }

    public String getDistance() {
        return distance;
    }

    @Override
    public String getViewType() {
        return "reimbursable-leg";
    }
}
