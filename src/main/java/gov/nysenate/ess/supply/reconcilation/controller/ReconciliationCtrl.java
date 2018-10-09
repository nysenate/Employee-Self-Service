package gov.nysenate.ess.supply.reconcilation.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.supply.reconcilation.model.Inventory;
import gov.nysenate.ess.supply.reconcilation.model.InventoryView;
import gov.nysenate.ess.supply.reconcilation.model.ReconciliationResults;
import gov.nysenate.ess.supply.reconcilation.model.ReconciliationResultsView;
import gov.nysenate.ess.supply.reconcilation.service.ReconciliationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/reconciliation")
public class ReconciliationCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(ReconciliationCtrl.class);

    @Autowired
    private ReconciliationService reconciliationService;

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse submitRecOrder(@RequestBody InventoryView inventoryView) {
        Inventory inventory = inventoryView.toInventory();
        ReconciliationResults results = reconciliationService.reconcile(inventory);
        return new ViewObjectResponse<>(new ReconciliationResultsView(results));
    }
}
