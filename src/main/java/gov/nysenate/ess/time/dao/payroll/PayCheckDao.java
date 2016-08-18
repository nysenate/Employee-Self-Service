package gov.nysenate.ess.time.dao.payroll;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.BaseDao;
import gov.nysenate.ess.time.model.payroll.Paycheck;

import java.time.LocalDate;
import java.util.List;

public interface PayCheckDao extends BaseDao
{
    /**
     * Get all employee paychecks for a given year.
     * @param empId employee id.
     * @param year year.
     * @return
     */
    List<Paycheck> getEmployeePaychecksForYear(int empId, int year);

    /**
     * Get all employee paychecks paid for dates within the given date range
     * @param empId int - employee id
     * @param dateRange Range<LocalDate> - date range
     * @return List<Paycheck>
     */
    List<Paycheck> getEmployeePaychecksForDates(int empId, Range<LocalDate> dateRange);
}
