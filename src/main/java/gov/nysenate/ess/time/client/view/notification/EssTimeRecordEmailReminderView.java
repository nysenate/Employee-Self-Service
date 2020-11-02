package gov.nysenate.ess.time.client.view.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.client.view.attendance.TimeRecordView;
import gov.nysenate.ess.time.model.notification.EssTimeRecordEmailReminder;

import java.util.List;
import java.util.stream.Collectors;

public class EssTimeRecordEmailReminderView implements ViewObject {

    private EmployeeView employee;
    private List<TimeRecordView> timeRecords;
    private boolean wasReminderSent;

    public EssTimeRecordEmailReminderView() {
    }

    public EssTimeRecordEmailReminderView(EssTimeRecordEmailReminder reminder) {
        this.employee = new EmployeeView(reminder.getEmployee());
        this.timeRecords = reminder.getTimeRecords().stream()
                .map(r -> new TimeRecordView(r, reminder.getEmployee()))
                .collect(Collectors.toList());
        this.wasReminderSent = reminder.wasReminderSent();
    }

    public EmployeeView getEmployee() {
        return employee;
    }

    public List<TimeRecordView> getTimeRecords() {
        return timeRecords;
    }

    @JsonProperty("wasReminderSent")
    public boolean wasReminderSent() {
        return wasReminderSent;
    }

    @Override
    public String getViewType() {
        return "ess-time-record-email-reminder";
    }
}
