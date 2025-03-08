package gov.nysenate.ess.time.service.attendance;

import java.time.LocalDate;

public class TimeRecordNotFoundEidBeginDateEx extends RuntimeException {

    private final Integer empId;
    private final LocalDate beginDate;

    public TimeRecordNotFoundEidBeginDateEx(Integer empId, LocalDate beginDate) {
        super("Could not find record for " + empId + " with begin date of " + beginDate);
        this.empId = empId;
        this.beginDate = beginDate;
    }

    public Integer getEmpId() {
        return empId;
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }
}
