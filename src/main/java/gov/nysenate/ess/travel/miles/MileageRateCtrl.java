package gov.nysenate.ess.travel.miles;

import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseRestApiCtrl.ADMIN_REST_PATH + "/travel/miles/")
public class MileageRateCtrl {

    @Autowired
    MileageAllowanceService allowanceService;

    @RequestMapping(value = "")
    public void updateMileageRate() {
        allowanceService.ensureCurrentMileageRate();
    }

}
