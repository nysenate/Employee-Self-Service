package gov.nysenate.ess.travel.application.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.model.GsaReimbursement;

public class GsaReimbursementView implements ViewObject {

    private String meals;
    private String lodging;
    private String incidental;
    private String total;

    public GsaReimbursementView(GsaReimbursement gsa) {
        this.meals = gsa.getMeals().toString();
        this.lodging = gsa.getLodging().toString();
        this.incidental = gsa.getIncidental().toString();
        this.total = gsa.total().toString();
    }

    public GsaReimbursement toGsaReimbursement() {
        return new GsaReimbursement(meals, lodging, incidental);
    }

    public String getMeals() {
        return meals;
    }

    public String getLodging() {
        return lodging;
    }

    public String getIncidental() {
        return incidental;
    }

    public String getTotal() {
        return total;
    }

    @Override
    public String getViewType() {
        return "gsa-reimbursement";
    }
}
