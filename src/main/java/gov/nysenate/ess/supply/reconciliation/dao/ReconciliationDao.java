package gov.nysenate.ess.supply.reconciliation.dao;


import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.supply.reconciliation.Reconciliation;

/**
 * Created by Chenguang He on 7/28/2016.
 */
public interface ReconciliationDao {
    /**
     * get Item by itemCategory
     *
     * @return Reconciliation
     */
    ImmutableList<Reconciliation> getReconciliation();

}
