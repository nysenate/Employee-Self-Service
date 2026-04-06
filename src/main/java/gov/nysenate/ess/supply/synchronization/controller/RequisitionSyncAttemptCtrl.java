package gov.nysenate.ess.supply.synchronization.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.supply.synchronization.model.RequisitionSyncAttempt;
import gov.nysenate.ess.supply.synchronization.service.RequisitionSyncAttemptService;
import gov.nysenate.ess.supply.synchronization.view.RequisitionSyncAttemptView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/requisition/syncAttempts")
public class RequisitionSyncAttemptCtrl extends BaseRestApiCtrl {
    private final RequisitionSyncAttemptService requisitionSyncAttemptService;

    @Autowired
    public RequisitionSyncAttemptCtrl(RequisitionSyncAttemptService requisitionSyncAttemptService) {
        this.requisitionSyncAttemptService = requisitionSyncAttemptService;
    }

    @RequestMapping(value = "/{requisitionId}", method = RequestMethod.GET)
    public ResponseEntity<List<RequisitionSyncAttempt>> getSyncAttemptById(@PathVariable int requisitionId) {
        List<RequisitionSyncAttempt> requisitionSyncAttempts = requisitionSyncAttemptService.getSyncAttemptsByReqId(requisitionId);
        if (requisitionSyncAttempts.isEmpty()) {
            return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(requisitionSyncAttempts, HttpStatus.OK);
    }
}
