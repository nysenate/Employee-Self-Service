package gov.nysenate.ess.travel.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.format.DateTimeFormatter;

public class TravelApplicationStatusView implements ViewObject {

    private boolean isPending;
    private boolean isApproved;
    private boolean isDisapproved;
    private String note;
    private String dateTime;

    public TravelApplicationStatusView() {
    }

    public TravelApplicationStatusView(TravelApplicationStatus status) {
        isPending = status.isPending();
        isApproved = status.isApproved();
        isDisapproved = status.isDisapproved();
        note = status.note();
        dateTime = status.dateTime().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    @JsonProperty("isPending")
    public boolean isPending() {
        return isPending;
    }

    @JsonProperty("isApproved")
    public boolean isApproved() {
        return isApproved;
    }

    @JsonProperty("isDisapproved")
    public boolean isDisapproved() {
        return isDisapproved;
    }

    public String getNote() {
        return note;
    }

    public String getDateTime() {
        return dateTime;
    }

    @Override
    public String getViewType() {
        return "travel application status";
    }
}
