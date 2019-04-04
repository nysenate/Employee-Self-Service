package gov.nysenate.ess.travel.application.allowances.lodging;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.format.DateTimeFormatter;

public class LodgingPerDiemView implements ViewObject {

    private String date;
    private AddressView address;
    private String dollars;
    private boolean reimbursementRequested;
    private String totalRequestedAllowance;

    public LodgingPerDiemView() {
    }

    public LodgingPerDiemView(LodgingPerDiem lpd) {
        this.date = lpd.getDate().format(DateTimeFormatter.ISO_DATE);
        this.address = new AddressView(lpd.getAddress());
        this.dollars = lpd.getDollars().toString();
        this.reimbursementRequested = lpd.isReimbursementRequested();
        this.totalRequestedAllowance = lpd.totalRequestedAllowance().toString();
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
        return "lodging-per-diem";
    }
}
