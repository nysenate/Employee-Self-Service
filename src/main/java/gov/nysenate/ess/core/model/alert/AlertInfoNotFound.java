package gov.nysenate.ess.core.model.alert;

public class AlertInfoNotFound extends RuntimeException {

    private int empId;

    public AlertInfoNotFound(int empId) {
        super("Could not find alert info for employee: " + empId);
        this.empId = empId;
    }

    public int getEmpId() {
        return empId;
    }
}
