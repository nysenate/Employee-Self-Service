package gov.nysenate.ess.travel.application.allowances.lodging;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.format.DateTimeFormatter;

public class LodgingPerDiemView implements ViewObject {

    private String date;
    private AddressView address;
    private String rate;
    private boolean reimbursementRequested;
    private String requestedAllowance;
    private String maximumAllowance;

    public LodgingPerDiemView() {
    }

    public LodgingPerDiemView(LodgingPerDiem lpd) {
        this.date = lpd.date().format(DateTimeFormatter.ISO_DATE);
        this.address = new AddressView(lpd.address());
        this.rate = lpd.rate().toString();
        this.reimbursementRequested = lpd.isReimbursementRequested();
        this.requestedAllowance = lpd.requestedAllowance().toString();
        this.maximumAllowance = lpd.maximumAllowance().toString();
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
        return "lodging-per-diem";
    }
}
