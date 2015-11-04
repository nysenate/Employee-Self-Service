package gov.nysenate.ess.web.service.payroll;

import gov.nysenate.ess.web.model.payroll.Paycheck;

import java.util.List;

public interface PaycheckService
{
    List<Paycheck> getEmployeePaychecksForYear(int empId, int year);
}
