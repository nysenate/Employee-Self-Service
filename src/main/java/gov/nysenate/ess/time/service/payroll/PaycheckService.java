package gov.nysenate.ess.time.service.payroll;

import gov.nysenate.ess.time.model.payroll.Paycheck;

import java.util.List;

/**
 * Service for retrieving employee paycheck data
 */
public interface PaycheckService
{
    /**
     * Gets all paychecks for the given employee for the given year
     *
     * @param empId int - employee id
     * @param year int - year
     * @return List<Paycheck>
     */
    List<Paycheck> getEmployeePaychecksForYear(int empId, int year);

    /**
     * Gets all paychecks for the given employee for the given fiscal year
     *
     * @param empId   int - employee id
     * @param endYear int - last year of the requested fiscal year
     * @return List<Paycheck>
     */
    List<Paycheck> getEmployeePaychecksForFiscalYear(int empId, int endYear);
}
