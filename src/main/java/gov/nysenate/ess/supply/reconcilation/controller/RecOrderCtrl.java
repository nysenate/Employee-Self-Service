package gov.nysenate.ess.supply.reconcilation.controller;


import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.supply.reconcilation.model.RecOrder;
import gov.nysenate.ess.supply.requisition.controller.RequisitionRestApiCtrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/reconciliation")
public class RecOrderCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(RequisitionRestApiCtrl.class);






}
