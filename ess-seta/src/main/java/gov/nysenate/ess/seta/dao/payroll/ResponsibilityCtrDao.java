package gov.nysenate.ess.seta.dao.payroll;

import gov.nysenate.ess.core.dao.base.BaseDao;
import gov.nysenate.ess.core.model.personnel.RespCtrException;
import gov.nysenate.ess.core.model.personnel.ResponsibilityCenter;

/**
 * Provides retrieval of ResponsibilityCenter information.
 */
public interface ResponsibilityCtrDao extends BaseDao
{
    /**
     * Retrieve the ResponsibilityCenter corresponding to the agency code and the resp center head.
     * If there are both active and inactive centers, the active one will be chosen. An error will be
     * thrown if there are either no matches or multiple matches that cannot be resolved.
     * @param agencyCode String - agency code
     * @param respCtrHead String - responsibility center head code
     * @return ResponsibilityCenter if single record found, throws RespCtrException otherwise.
     * @throws RespCtrException - RespCtrNotFoundEx if no records found
     *                          - RespCtrMultipleMatchesEx if multiple records exist for the given agency/head.
     */
    public ResponsibilityCenter getRespCtr(String agencyCode, String respCtrHead) throws RespCtrException;
}
