package gov.nysenate.ess.time.dao.allowances;

import gov.nysenate.ess.core.dao.base.BaseDao;
import gov.nysenate.ess.time.model.allowances.OldAllowanceUsage;

/**
 * Data access layer for retrieving and computing allowance information
 * (e.g temporary employee yearly allowances, hours used).
 */
public interface AllowanceDao extends BaseDao
{
    OldAllowanceUsage getAllowanceUsage(int empId, int year);
}