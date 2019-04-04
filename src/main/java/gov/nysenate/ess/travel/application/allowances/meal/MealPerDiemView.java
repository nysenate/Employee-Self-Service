package gov.nysenate.ess.travel.application.allowances.meal;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.format.DateTimeFormatter;

public class MealPerDiemView implements ViewObject {

    private String date;
    private AddressView address;
    private String rate;
    private boolean reimbursementRequested;
    private String requestedAllowance;
    private String maximumAllowance;

    public MealPerDiemView() {
    }

    public MealPerDiemView(MealPerDiem mpd) {
        this.date = mpd.date().format(DateTimeFormatter.ISO_DATE);
        this.address = new AddressView(mpd.address());
        this.rate = mpd.rate().toString();
        this.reimbursementRequested = mpd.isReimbursementRequested();
        this.requestedAllowance = mpd.requestedAllowance().toString();
        this.maximumAllowance = mpd.maximumAllowance().toString();
    }

    public String getDate() {
        return date;
    }

    public AddressView getAddress() {
        return address;
    }

    public String getRate() {
        return rate;
    }

    public boolean isReimbursementRequested() {
        return reimbursementRequested;
    }

    public String getRequestedAllowance() {
        return requestedAllowance;
    }

    public String getMaximumAllowance() {
        return maximumAllowance;
    }

    @Override
    public String getViewType() {
        return "meal-per-diem";
    }
}
