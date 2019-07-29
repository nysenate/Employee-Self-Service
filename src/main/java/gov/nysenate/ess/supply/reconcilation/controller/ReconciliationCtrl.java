package gov.nysenate.ess.supply.reconcilation.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import gov.nysenate.ess.supply.reconcilation.ReconciliationDtoView;
import gov.nysenate.ess.supply.reconcilation.model.Inventory;
import gov.nysenate.ess.supply.reconcilation.model.InventoryView;
import gov.nysenate.ess.supply.reconcilation.model.ReconciliationResults;
import gov.nysenate.ess.supply.reconcilation.model.ReconciliationResultsView;
import gov.nysenate.ess.supply.reconcilation.service.ReconciliationService;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/reconciliation")
public class ReconciliationCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(ReconciliationCtrl.class);

    @Autowired private ReconciliationService reconciliationService;

    /**
     * Create and return an Inventory containing all items which need to be reconciled.
     * Quantities are initialized to 0.
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse requestReconciliation() {
        List<Requisition> pendingReconciliation = reconciliationService.reqsPendingReconciliation();
        Set<SupplyItem> itemsToBeReconciled = reconciliationService.itemsRequiringReconciliation(pendingReconciliation);
        Set<Integer> itemIds = itemsToBeReconciled.stream()
                .mapToInt(SupplyItem::getId)
                .boxed()
                .collect(Collectors.toSet());
        Inventory emptyInventory = new Inventory(itemIds);

        return new ViewObjectResponse<>(
                new ReconciliationDtoView(pendingReconciliation, itemsToBeReconciled, emptyInventory));
    }

    /**
     * Submit an inventory to be reconciled.
     * @param inventoryView
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse submitReconciliation(@RequestBody InventoryView inventoryView) {
        Inventory inventory = inventoryView.toInventory();
        ReconciliationResults results = reconciliationService.reconcile(inventory);
        return new ViewObjectResponse<>(new ReconciliationResultsView(results));
    }
}
