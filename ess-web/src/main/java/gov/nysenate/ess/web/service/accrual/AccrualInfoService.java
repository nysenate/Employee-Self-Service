package gov.nysenate.ess.web.service.accrual;

import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.seta.model.accrual.AnnualAccSummary;
import gov.nysenate.ess.core.model.period.PayPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

public interface AccrualInfoService
{
    /**
     * Returns a map of Annual Accrual Summaries (PM23ATTEND) keyed by the year.
     *
     * @param empId int - Employee id
     * @param endYear int - The most recent year to fetch up to
     * @return TreeMap<Integer, AnnualAccSummary>
     */
    TreeMap<Integer, AnnualAccSummary> getAnnualAccruals(int empId, int endYear);

    /**
     * Attendance years are closed out and finalized at some point after the year has ended. The pay periods
     * that belong to an open year are of interest because the employee can still submit attendance records
     * for them. The date at which a year closes out is specific to an employee and therefore the empId is
     * required.
     *
     * @param empId int - Employee id
     * @param endDate LocalDate - The retrieved pay periods will have a range before or during this date.
     * @param dateOrder SortOrder - Order by the start date of the pay period
     * @return List<PayPeriod>
     */
    List<PayPeriod> getActiveAttendancePeriods(int empId, LocalDate endDate, SortOrder dateOrder);
}