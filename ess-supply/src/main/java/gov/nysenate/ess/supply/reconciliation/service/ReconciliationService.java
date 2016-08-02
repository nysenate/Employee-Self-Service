package gov.nysenate.ess.supply.reconciliation.service;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.supply.reconciliation.Reconciliation;

/**
 * Created by Chenguang He on 8/3/2016.
 */
public interface ReconciliationService {
    ImmutableList<Reconciliation> getRequisition();
}
