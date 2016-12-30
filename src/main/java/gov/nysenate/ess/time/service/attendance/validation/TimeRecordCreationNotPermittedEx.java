package gov.nysenate.ess.time.service.attendance.validation;

import gov.nysenate.ess.core.model.period.PayPeriod;

/**
 * Exception thrown when creation of a time record is not permitted for a specific pay period
 */
public class TimeRecordCreationNotPermittedEx extends RuntimeException {

    private int empId;
    private PayPeriod payPeriod;

    public TimeRecordCreationNotPermittedEx(int empId, PayPeriod payPeriod) {
        super("Cannot create new time record for employee #" + empId + " on period: " + payPeriod);
        this.empId = empId;
        this.payPeriod = payPeriod;
    }

    public int getEmpId() {
        return empId;
    }

    public PayPeriod getPayPeriod() {
        return payPeriod;
    }
}
