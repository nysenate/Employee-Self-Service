package gov.nysenate.ess.travel.approval;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/approval")
public class ApplicationApprovalCtrl extends BaseRestApiCtrl {

    /**
     * Get approvals which need approval by the logged in user.
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse getPendingApprovals() {
        getSubject().is
        int userEmpId = getSubjectEmployeeId();
        return null;
    }
}
