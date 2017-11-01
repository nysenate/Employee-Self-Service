package gov.nysenate.ess.travel.allowance.gsa;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.allowance.gsa.model.GsaAllowance;

public class GsaAllowanceView implements ViewObject {

    private String meals;
    private String lodging;
    private String incidental;
    private String total;

    private GsaAllowanceView() {
    }

    public GsaAllowanceView(GsaAllowance gsa) {
        this.meals = gsa.getMeals().toString();
        this.lodging = gsa.getLodging().toString();
        this.incidental = gsa.getIncidental().toString();
        this.total = gsa.total().toString();
    }

    public GsaAllowance toGsaAllowance() {
        return new GsaAllowance(meals, lodging, incidental);
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
        return "gsa-allowance";
    }
}
