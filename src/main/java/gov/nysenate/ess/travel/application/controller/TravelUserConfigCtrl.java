package gov.nysenate.ess.travel.application.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/user/config")
public class TravelUserConfigCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(TravelUserConfigCtrl.class);

    @RequestMapping(value = "", method = {RequestMethod.GET, RequestMethod.HEAD}, params = "empId")
    public BaseResponse getConfig(@RequestParam(required = true) Integer empId[]){

        return null;
    }
}
