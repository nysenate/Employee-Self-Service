package gov.nysenate.ess.time.service.attendance;

import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;

/**
 * Event that is posted when a user performs an action on a time record.
 */
public class TimeRecordActionEvent {

    private final TimeRecord timeRecord;
    private final TimeRecordAction timeRecordAction;

    public TimeRecordActionEvent(TimeRecord timeRecord, TimeRecordAction timeRecordAction) {
        this.timeRecord = timeRecord;
        this.timeRecordAction = timeRecordAction;
    }

    public TimeRecord getTimeRecord() {
        return timeRecord;
    }

    public TimeRecordAction getTimeRecordAction() {
        return timeRecordAction;
    }
}
