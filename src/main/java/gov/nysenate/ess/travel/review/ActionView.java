package gov.nysenate.ess.travel.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.format.DateTimeFormatter;

public class ActionView implements ViewObject {

    private EmployeeView user;
    private String role;
    private boolean isApproval;
    private boolean isDisapproval;
    private String notes;
    private String dateTime;

    public ActionView() {
    }

    public ActionView(Action action) {
        this.user = new EmployeeView(action.user());
        this.role = action.role().name();
        this.notes = action.notes();
        this.isApproval = action.isApproval();
        this.isDisapproval = action.isDisapproval();
        this.dateTime = action.dateTime().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public EmployeeView getUser() {
        return user;
    }

    public String getRole() {
        return role;
    }

    @JsonProperty("isApproval")
    public boolean isApproval() {
        return isApproval;
    }

    @JsonProperty("isDisapproval")
    public boolean isDisapproval() {
        return isDisapproval;
    }

    public String getNotes() {
        return notes;
    }

    public String getDateTime() {
        return dateTime;
    }

    @Override
    public String getViewType() {
        return "application-review-approval";
    }
}
