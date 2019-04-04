package gov.nysenate.ess.travel.application.allowances.meal;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.format.DateTimeFormatter;

public class MealPerDiemView implements ViewObject {

    private String date;
    private AddressView address;
    private String dollars;
    private boolean reimbursementRequested;
    private String totalRequestedAllowance;

    public MealPerDiemView() {
    }

    public MealPerDiemView(MealPerDiem mpd) {
        this.date = mpd.getDate().format(DateTimeFormatter.ISO_DATE);
        this.address = new AddressView(mpd.getAddress());
        this.dollars = mpd.getDollars().toString();
        this.reimbursementRequested = mpd.isReimbursementRequested();
        this.totalRequestedAllowance = mpd.totalRequestedAllowance().toString();
    }

    public String getDate() {
        return date;
    }

    public AddressView getAddress() {
        return address;
    }

    public String getDollars() {
        return dollars;
    }

    public boolean isReimbursementRequested() {
        return reimbursementRequested;
    }

    public String getTotalRequestedAllowance() {
        return totalRequestedAllowance;
    }

    @Override
    public String getViewType() {
        return "meal-per-diem";
    }
}
