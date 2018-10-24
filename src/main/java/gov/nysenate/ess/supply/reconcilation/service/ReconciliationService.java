package gov.nysenate.ess.supply.reconcilation.service;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.reconcilation.ReconciliationException;
import gov.nysenate.ess.supply.reconcilation.dao.OracleInventoryDao;
import gov.nysenate.ess.supply.reconcilation.model.Inventory;
import gov.nysenate.ess.supply.reconcilation.model.ReconciliationResults;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.model.RequisitionQuery;
import gov.nysenate.ess.supply.requisition.model.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;


@Service
public class ReconciliationService {

    private Reconciler reconciler;
    private OracleInventoryDao oracleInventoryDao;
    private RequisitionService requisitionService;

    @Autowired
    public ReconciliationService(Reconciler reconciler, OracleInventoryDao oracleInventoryDao,
                                 RequisitionService requisitionService) {
        this.reconciler = reconciler;
        this.oracleInventoryDao = oracleInventoryDao;
        this.requisitionService = requisitionService;
    }

    public ReconciliationResults reconcile(Inventory expectedInventory) {
        List<Requisition> reqsPendingReconciliation = reqsPendingReconciliation();
        if (!isFullInventory(expectedInventory, reqsPendingReconciliation)) {
            throw new ReconciliationException("Inventory did not include counts for all needed items");
        }

        Inventory actualInventory = oracleInventoryDao.forLocation(expectedInventory.getLocationId());
        ReconciliationResults results = reconciler.reconcile(expectedInventory, actualInventory);

        if (results.success()) {
            for (Requisition req : reqsPendingReconciliation) {
                requisitionService.reconcileRequisition(req);
            }
        }

        return results;
    }

    private List<Requisition> reqsPendingReconciliation() {
        RequisitionQuery query = new RequisitionQuery()
                .setStatuses(EnumSet.of(RequisitionStatus.APPROVED))
                .setReconciled("false")
                .setLimitOffset(LimitOffset.ALL);
        return requisitionService.searchRequisitions(query).getResults();
    }

    /**
     * Verifies all items needed to reconcile the outstanding requisitions are included in the Inventory.
     * @param expectedInventory
     * @param needsReconciliation
     * @return
     */
    private boolean isFullInventory(Inventory expectedInventory, List<Requisition> needsReconciliation) {
        return needsReconciliation.stream()
                .map(Requisition::getLineItems)
                .flatMap(Set::stream)
                .filter(li -> li.getItem().requiresSynchronization()) // Remove items for which inventory counts are not tracked.
                .allMatch(li -> expectedInventory.containsItem(li.getItem().getId()));
    }
}
