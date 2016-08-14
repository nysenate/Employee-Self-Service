package gov.nysenate.ess.supply.reconciliation.service;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.supply.reconciliation.Reconciliation;
import gov.nysenate.ess.supply.reconciliation.dao.ReconciliationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Chenguang He on 8/3/2016.
 */
@Service
public class SupplyReconciliationService implements ReconciliationService {

    @Autowired
    private ReconciliationDao reconciliationDao;

    @Override
    public ImmutableList<Reconciliation> getRequisition() {
        return reconciliationDao.getReconciliation();
    }
}
