package gov.nysenate.ess.travel.approval;

import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.format.DateTimeFormatter;

public class ActionView implements ViewObject {

    private EmployeeView user;
    private String role;
    private String type;
    private String notes;
    private String dateTime;

    public ActionView() {
    }

    public ActionView(Action action) {
        this.user = new EmployeeView(action.user());
        this.role = action.role().name();
        this.notes = action.notes();
        this.type = action.type().name();
        this.dateTime = action.dateTime().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public EmployeeView getUser() {
        return user;
    }

    public String getRole() {
        return role;
    }

    public String getType() {
        return type;
    }

    public String getNotes() {
        return notes;
    }

    public String getDateTime() {
        return dateTime;
    }

    @Override
    public String getViewType() {
        return "approval";
    }
}
