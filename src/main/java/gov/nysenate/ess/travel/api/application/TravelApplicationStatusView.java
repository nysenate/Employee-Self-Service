package gov.nysenate.ess.travel.api.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.request.app.ApprovalStatus;
import gov.nysenate.ess.travel.request.app.TravelApplicationStatus;

public class TravelApplicationStatusView implements ViewObject {

    @JsonProperty("isPending")
    private boolean isPending;
    @JsonProperty("isApproved")
    private boolean isApproved;
    @JsonProperty("isDisapproved")
    private boolean isDisapproved;
    @JsonProperty("isNotApplicable")
    private boolean isNotApplicable;
    private String note;

    public TravelApplicationStatusView() {
    }

    public TravelApplicationStatusView(TravelApplicationStatus status) {
        isPending = status.isPending();
        isApproved = status.isApproved();
        isDisapproved = status.isDisapproved();
        isNotApplicable = status.isNotApplicable();
        note = status.note();
    }

    public TravelApplicationStatus toTravelApplicationStatus() {
        ApprovalStatus status = isPending ? ApprovalStatus.PENDING
                : isApproved ? ApprovalStatus.APPROVED
                : isDisapproved ? ApprovalStatus.DISAPPROVED
                : isNotApplicable ? ApprovalStatus.NOT_APPLICABLE
                : null;
        if (status == null) {
            throw new IllegalArgumentException("TravelApplicationStatus ApplicationStatus cannot be null." +
                    " Likely an error in view serialization/deserialization.");
        }

        return new TravelApplicationStatus(status, note);
    }

    @JsonIgnore
    public boolean isPending() {
        return isPending;
    }

    @JsonIgnore
    public boolean isApproved() {
        return isApproved;
    }

    @JsonIgnore
    public boolean isDisapproved() {
        return isDisapproved;
    }

    @JsonIgnore
    public boolean isNotApplicable() {
        return isNotApplicable;
    }

    public String getNote() {
        return note;
    }

    @Override
    public String getViewType() {
        return "travel application status";
    }
}
