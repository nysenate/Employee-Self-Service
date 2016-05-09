package gov.nysenate.ess.supply.allowance.dao;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.allowance.ItemAllowance;

import java.util.Set;

public interface ItemAllowanceDao {

    Set<ItemAllowance> getItemAllowances(LocationId locationId);
}
