package gov.nysenate.ess.time.dao.attendance;

import gov.nysenate.ess.core.model.period.PayPeriod;

public class AttendanceRecordNotFoundEx extends RuntimeException {

    private static final long serialVersionUID = 3658825255348853642L;

    private int empId;
    private PayPeriod payPeriod;

    public AttendanceRecordNotFoundEx(int empId, PayPeriod payPeriod) {
        super("Could not find attendance record for emp: " + empId + " period: " + payPeriod);
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
