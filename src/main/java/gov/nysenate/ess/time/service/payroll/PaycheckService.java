package gov.nysenate.ess.time.service.payroll;

import gov.nysenate.ess.time.model.payroll.Paycheck;

import java.util.List;

public interface PaycheckService
{
    List<Paycheck> getEmployeePaychecksForYear(int empId, int year);
}
