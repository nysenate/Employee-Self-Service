package gov.nysenate.ess.core.model.emergency_notification;

public class EmergencyNotificationInfoNotFound extends RuntimeException {

    private int empId;

    public EmergencyNotificationInfoNotFound(int empId) {
        super("Could not find emergency notification info for employee: " + empId);
        this.empId = empId;
    }

    public int getEmpId() {
        return empId;
    }
}
