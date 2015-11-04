package gov.nysenate.ess.web.dao.payroll;

import gov.nysenate.ess.core.dao.base.BaseDao;
import gov.nysenate.ess.web.model.payroll.Paycheck;

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
}
