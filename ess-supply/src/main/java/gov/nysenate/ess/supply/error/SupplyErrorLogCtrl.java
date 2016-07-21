package gov.nysenate.ess.supply.error;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/error")
public class SupplyErrorLogCtrl {

    private static final Logger logger = LoggerFactory.getLogger(SupplyErrorLogCtrl.class);

    @Autowired private SupplyErrorLogService errorLogService;

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "text/plain")
    public BaseResponse logErrorMessage(@RequestBody String message) {
        logger.error("Supply Error: " + message);
        errorLogService.saveError(message);
        return new SimpleResponse();
    }
}
