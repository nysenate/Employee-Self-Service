package gov.nysenate.ess.travel.accommodation;

import gov.nysenate.ess.core.client.view.base.ViewObject;

public class StayView implements ViewObject {

//    private final String date;
//    private final String lodgingAllowance;
//    private final String mealAllowance;

    @Override
    public String getViewType() {
        return "stay";
    }
}
