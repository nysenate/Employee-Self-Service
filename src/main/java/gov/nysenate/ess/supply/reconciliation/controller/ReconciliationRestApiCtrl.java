package gov.nysenate.ess.supply.reconciliation.controller;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.supply.reconciliation.Reconciliation;
import gov.nysenate.ess.supply.reconciliation.service.SupplyReconciliationService;
import gov.nysenate.ess.supply.reconciliation.view.ReconciliationView;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

/**
 * Created by Chenguang He on 8/3/2016.
 */
@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/reconciliation")
public class ReconciliationRestApiCtrl extends BaseRestApiCtrl {
    private static final Logger logger = LoggerFactory.getLogger(ReconciliationRestApiCtrl.class);
    @Autowired
    private SupplyReconciliationService supplyReconciliationService;

    @RequestMapping("")
    public BaseResponse getRequisitionById(@RequestParam(required = false) String status) {
        checkPermission(new WildcardPermission("supply:employee"));
        ImmutableList<Reconciliation> reconciliations = supplyReconciliationService.getRequisition();
        return ListViewResponse.of(reconciliations.stream().map(ReconciliationView::new).collect(Collectors.toList()));
    }
}
