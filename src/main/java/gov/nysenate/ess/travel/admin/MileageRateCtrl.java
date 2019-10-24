package gov.nysenate.ess.travel.admin;

import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import gov.nysenate.ess.travel.provider.miles.MileageAllowanceService;
import gov.nysenate.ess.travel.provider.miles.MileageRateView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseRestApiCtrl.ADMIN_REST_PATH + "/travel/miles/")
public class MileageRateCtrl extends BaseRestApiCtrl {

    @Autowired
    MileageAllowanceService allowanceService;

    @RequestMapping(value = "")
    public MileageRateView updateMileageRate() {
        checkPermission(SimpleEssPermission.ADMIN.getPermission());
        return new MileageRateView(allowanceService.scrapeCurrentMileageRate());
    }

}
