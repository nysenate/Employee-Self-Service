package gov.nysenate.ess.travel.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class TravelApplicationStatusView implements ViewObject {

    @JsonProperty("isPending")
    private boolean isPending;
    @JsonProperty("isApproved")
    private boolean isApproved;
    @JsonProperty("isDisapproved")
    private boolean isDisapproved;
    private String note;

    public TravelApplicationStatusView() {
    }

    public TravelApplicationStatusView(TravelApplicationStatus status) {
        isPending = status.isPending();
        isApproved = status.isApproved();
        isDisapproved = status.isDisapproved();
        note = status.note();
    }

    public TravelApplicationStatus toTravelApplicationStatus() {
        TravelApplicationStatus.ApplicationStatus status = isPending ? TravelApplicationStatus.ApplicationStatus.PENDING
                : isApproved ? TravelApplicationStatus.ApplicationStatus.APPROVED
                : isDisapproved ? TravelApplicationStatus.ApplicationStatus.DISAPPROVED
                : null;
        if (status == null) {
            throw new IllegalArgumentException("TravelApplicationStatus ApplicationStatus cannot be null." +
                    " Likely an error in view serialization/deserialization.");
        }

        return new TravelApplicationStatus(status, note);
    }

    public boolean isPending() {
        return isPending;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public boolean isDisapproved() {
        return isDisapproved;
    }

    public String getNote() {
        return note;
    }

    @Override
    public String getViewType() {
        return "travel application status";
    }
}
