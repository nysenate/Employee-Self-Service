package gov.nysenate.ess.supply.synchronization.admin;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import gov.nysenate.ess.supply.synchronization.service.SfmsSynchronizationService;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/admin/synchronization")
public class SupplySynchronizationRestCtrl extends BaseRestApiCtrl {

    @Autowired private SfmsSynchronizationService synchronizationService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public BaseResponse runSynchronization() {
        checkPermission(SimpleEssPermission.ADMIN.getPermission());
        synchronizationService.synchronizeRequisitions();
        return new SimpleResponse(true, "Supply synchronization has been run.", "run-synchronization");
    }
}
