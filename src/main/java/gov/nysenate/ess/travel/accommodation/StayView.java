package gov.nysenate.ess.travel.accommodation;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import static java.time.format.DateTimeFormatter.*;

public class StayView implements ViewObject {

    private String date;
    private String lodgingAllowance;
    private String mealAllowance;

    public StayView() {
    }

    public StayView(Stay stay) {
        date = stay.getDate().format(ISO_DATE);
        lodgingAllowance = stay.lodgingAllowance().toString();
        mealAllowance = stay.mealAllowance().toString();
    }

    public String getDate() {
        return date;
    }

    public String getLodgingAllowance() {
        return lodgingAllowance;
    }

    public String getMealAllowance() {
        return mealAllowance;
    }

    @Override
    public String getViewType() {
        return "stay";
    }
}
