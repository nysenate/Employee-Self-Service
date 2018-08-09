package gov.nysenate.ess.travel.application.allowances;

import gov.nysenate.ess.core.client.view.base.ViewObject;

public class AllowancesDtoView implements ViewObject {

    public String tollsAllowance;
    public String parkingAllowance;
    public String alternateAllowance;
    public String registrationAllowance;

    public AllowancesDtoView() {
    }

    public String getTollsAllowance() {
        return tollsAllowance;
    }

    public String getParkingAllowance() {
        return parkingAllowance;
    }

    public String getAlternateAllowance() {
        return alternateAllowance;
    }

    public String getRegistrationAllowance() {
        return registrationAllowance;
    }

    @Override
    public String getViewType() {
        return "allowances-dto-view";
    }
}
