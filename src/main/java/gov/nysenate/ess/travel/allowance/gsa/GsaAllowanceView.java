package gov.nysenate.ess.travel.allowance.gsa;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.allowance.gsa.model.GsaAllowance;

public class GsaAllowanceView implements ViewObject {

    private String meals;
    private String lodging;
    private String total;

    private GsaAllowanceView() {
    }

    public GsaAllowanceView(GsaAllowance gsa) {
        this.meals = gsa.getMeals().toString();
        this.lodging = gsa.getLodging().toString();
        this.total = gsa.total().toString();
    }

    public GsaAllowance toGsaAllowance() {
        return new GsaAllowance(meals, lodging);
    }

    public String getMeals() {
        return meals;
    }

    public String getLodging() {
        return lodging;
    }

    public String getTotal() {
        return total;
    }

    @Override
    public String getViewType() {
        return "gsa-allowance";
    }
}
