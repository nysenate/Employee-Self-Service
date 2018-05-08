package gov.nysenate.ess.travel.application.uncompleted;

import gov.nysenate.ess.core.client.view.base.ViewObject;

public class AllowancesDtoView implements ViewObject {

    String tollsAllowance;
    String parkingAllowance;
    String alternateAllowance;
    String registrationAllowance;

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
