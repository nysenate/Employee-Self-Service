package gov.nysenate.ess.supply.sfms;


import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.supply.error.SupplyErrorLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/sfms")
public class SupplySfmsRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(SupplySfmsRestApiCtrl.class);
}
