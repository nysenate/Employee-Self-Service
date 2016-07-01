package gov.nysenate.ess.seta.model.attendance;

public class InvalidTimeRecordActionEx extends RuntimeException {

    private static final long serialVersionUID = 1436675983194790361L;

    private TimeRecordStatus status;
    private TimeRecordAction action;

    public InvalidTimeRecordActionEx(TimeRecordStatus status, TimeRecordAction action) {
        super("Action " + action + " cannot be applied to time records with status " + status);
        this.status = status;
        this.action = action;
    }

    public TimeRecordStatus getStatus() {
        return status;
    }

    public TimeRecordAction getAction() {
        return action;
    }
}
