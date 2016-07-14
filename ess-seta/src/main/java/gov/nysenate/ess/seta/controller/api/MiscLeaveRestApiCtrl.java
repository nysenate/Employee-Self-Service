package gov.nysenate.ess.seta.controller.api;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.seta.client.view.MiscLeaveGrantView;
import gov.nysenate.ess.seta.dao.payroll.MiscLeaveDao;
import gov.nysenate.ess.seta.model.auth.EssTimePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

import static gov.nysenate.ess.seta.model.auth.TimePermissionObject.MISC_LEAVE_GRANT;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/miscleave")
public class MiscLeaveRestApiCtrl extends BaseRestApiCtrl {

    @Autowired MiscLeaveDao miscLeaveDao;

    @RequestMapping("/grants")
    public BaseResponse getMiscLeaveGrants(@RequestParam int empId) {
        checkPermission(new EssTimePermission(empId, MISC_LEAVE_GRANT, GET, Range.all()));
        return ListViewResponse.of(
                miscLeaveDao.getMiscLeaveGrants(empId).stream()
                        .map(MiscLeaveGrantView::new)
                        .collect(Collectors.toList())
        );
    }
}
