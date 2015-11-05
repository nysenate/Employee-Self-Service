package gov.nysenate.ess.seta.service.payroll;

import gov.nysenate.ess.seta.model.payroll.Paycheck;

import java.util.List;

public interface PaycheckService
{
    List<Paycheck> getEmployeePaychecksForYear(int empId, int year);
}
