package gov.nysenate.ess.seta.dao.allowances;

import gov.nysenate.ess.core.dao.base.BaseDao;
import gov.nysenate.ess.seta.model.allowances.OldAllowanceUsage;

/**
 * Data access layer for retrieving and computing allowance information
 * (e.g temporary employee yearly allowances, hours used).
 */
public interface AllowanceDao extends BaseDao
{
    OldAllowanceUsage getAllowanceUsage(int empId, int year);
}