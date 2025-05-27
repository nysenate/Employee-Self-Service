package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.view.CheckPermissionView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/permissions")
public class PermissionCtrl extends BaseRestApiCtrl {

    @RequestMapping(value = "/check", produces = "application/json")
    public BaseResponse checkPermission(@RequestParam String permission) {
        return new ViewObjectResponse<>(
                new CheckPermissionView(getSubjectEmployeeId(), permission, getSubject().isPermitted(permission))
        );
    }
}
