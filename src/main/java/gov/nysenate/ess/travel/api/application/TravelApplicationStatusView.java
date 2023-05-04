package gov.nysenate.ess.travel.api.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.request.app.ApprovalStatus;
import gov.nysenate.ess.travel.request.app.TravelApplicationStatus;

public class TravelApplicationStatusView implements ViewObject {

    private String name;
    private String label;
    private String note;

    @JsonProperty("isPending")
    private boolean isPending;
    @JsonProperty("isApproved")
    private boolean isApproved;
    @JsonProperty("isDisapproved")
    private boolean isDisapproved;
    @JsonProperty("isNotApplicable")
    private boolean isNotApplicable;
    @JsonProperty("isDraft")
    private boolean isDraft;

    public TravelApplicationStatusView() {
    }

    public TravelApplicationStatusView(TravelApplicationStatus status) {
        name = status.status().name();
        label = status.status().label();
        note = status.note();
        isPending = status.isPending();
        isApproved = status.isApproved();
        isDisapproved = status.isDisapproved();
        isNotApplicable = status.isNotApplicable();
        isDraft = status.isDraft();
    }

    public TravelApplicationStatus toTravelApplicationStatus() {
        ApprovalStatus status = ApprovalStatus.valueOf(name);
        return new TravelApplicationStatus(status, note);
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public String getNote() {
        return note;
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

    @JsonIgnore
    public boolean isDraft() {
        return isDraft;
    }

    @Override
    public String getViewType() {
        return "travel application status";
    }
}
