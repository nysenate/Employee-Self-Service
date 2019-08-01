package gov.nysenate.ess.supply.reconcilation.service;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import gov.nysenate.ess.supply.reconcilation.ReconciliationException;
import gov.nysenate.ess.supply.reconcilation.dao.OracleInventoryDao;
import gov.nysenate.ess.supply.reconcilation.model.Inventory;
import gov.nysenate.ess.supply.reconcilation.model.ReconciliationResults;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.model.RequisitionQuery;
import gov.nysenate.ess.supply.requisition.model.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReconciliationService {

    private static final LocationId SUPPLY_LOCATION_ID = new LocationId("LC100S", 'P');

    private Reconciler reconciler;
    private OracleInventoryDao oracleInventoryDao;
    private RequisitionService requisitionService;
    private HashSet<String> itemIgnoreList;

    @Autowired
    public ReconciliationService(Reconciler reconciler,
                                 OracleInventoryDao oracleInventoryDao,
                                 RequisitionService requisitionService,
                                 @Value("${supply.reconciliation.ignore.list}") String[] itemIgnoreList) {
        this.reconciler = reconciler;
        this.oracleInventoryDao = oracleInventoryDao;
        this.requisitionService = requisitionService;
        this.itemIgnoreList = Sets.newHashSet(itemIgnoreList);
    }

    public ReconciliationResults reconcile(Inventory inventory) {
        List<Requisition> reqsPendingReconciliation = reqsPendingReconciliation();
        if (!isFullInventory(inventory, reqsPendingReconciliation)) {
            throw new ReconciliationException("Inventory did not include counts for all needed items");
        }

        Inventory expectedInventory = oracleInventoryDao.forLocation(SUPPLY_LOCATION_ID);
        ReconciliationResults results = reconciler.reconcile(inventory, expectedInventory);

        if (results.success()) {
            for (Requisition req : reqsPendingReconciliation) {
                requisitionService.reconcileRequisition(req);
            }
        }

        return results;
    }

    public List<Requisition> reqsPendingReconciliation() {
        RequisitionQuery query = new RequisitionQuery()
                .setStatuses(EnumSet.of(RequisitionStatus.APPROVED))
                .setReconciled("false")
                .setFromDateTime(DateUtils.LONG_AGO.atTime(0, 0))
                .setLimitOffset(LimitOffset.ALL);
        return requisitionService.searchRequisitions(query).getResults();
    }

    /**
     * Verifies all items needed to reconcile the outstanding requisitions are included in the Inventory.
     *
     * An item needs to be reconciled if it requires synchronization.
     *
     * @param inventory User entered inventory.
     * @param reqs Requisitions needing to be reconciled.
     * @return
     */
    private boolean isFullInventory(Inventory inventory, List<Requisition> reqs) {
        return itemsRequiringReconciliation(reqs).stream()
                .allMatch(i -> inventory.containsItem(i.getId()));
    }

    /**
     * Given all requisitions which require reconciliation this returns
     * a Set of all items which need to be reconciled.
     *
     * Items that do not require synchronization do not need to be reconciled.
     * @param reqs
     * @return
     */
    public Set<SupplyItem> itemsRequiringReconciliation(List<Requisition> reqs) {
        return reqs.stream()
                .map(Requisition::getLineItems)
                .flatMap(Collection::stream)
                .map(LineItem::getItem)
                .filter(SupplyItem::requiresSynchronization)
                .filter(i -> !itemIgnoreList.contains(i.getCommodityCode()))
                .collect(Collectors.toSet());
    }
}
