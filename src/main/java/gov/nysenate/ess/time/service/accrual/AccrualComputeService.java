package gov.nysenate.ess.time.service.accrual;

import gov.nysenate.ess.time.model.accrual.AccrualException;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.time.model.accrual.PeriodAccSummary;

import java.util.Collection;
import java.util.TreeMap;

/**
 * Service interface to provide accrual related functionality.
 */
public interface AccrualComputeService
{
    /**
     * Computes the accruals available for an employee at the start of the given pay period.
     *
     * @param empId int - Employee id to get accruals for.
     * @param payPeriod PayPeriod - Accruals will be valid at the start of this pay period.
     * @return PeriodAccSummary
     * @throws AccrualException - If there is an exception during either retrieval or computation of the accruals.
     */
    public PeriodAccSummary getAccruals(int empId, PayPeriod payPeriod) throws AccrualException;

    /**
     * Retrieves a collection of accrual summaries for each of the pay periods within the given 'payPeriods'.
     *
     * @param empId int - Employee id to get accruals for.
     * @param payPeriods - PeriodAccSummaries will be valid at the start of each period in this list.
     * @return TreeMap<PayPeriod, PeriodAccSummary>
     * @throws AccrualException
     */
    public TreeMap<PayPeriod, PeriodAccSummary> getAccruals(int empId, Collection<PayPeriod> payPeriods) throws AccrualException;
}