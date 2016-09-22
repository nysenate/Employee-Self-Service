package gov.nysenate.ess.time.controller.api;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.util.AsyncRunner;
import gov.nysenate.ess.time.client.response.TimeRecordManagerResponse;
import gov.nysenate.ess.time.service.attendance.TimeRecordManager;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static gov.nysenate.ess.core.controller.api.BaseRestApiCtrl.ADMIN_REST_PATH;

@RestController
@RequestMapping(ADMIN_REST_PATH + "/time/timerecords")
public class TimeRecordManagementCtrl extends BaseRestApiCtrl {

    @Autowired TimeRecordManager timeRecordManager;
    @Autowired AsyncRunner runner;

    @RequiresPermissions("admin:time:timerecords:manager")
    @RequestMapping(value = "/manager", method = RequestMethod.POST)
    public BaseResponse runTimeRecordManager() {
        runner.run(timeRecordManager::ensureAllActiveRecords);
        return new SimpleResponse(true,
                "Kicked off time record manager, see logs for progress",
                "time-record-manager-run-success");
    }

    @RequiresPermissions("admin:time:timerecords:manager")
    @RequestMapping(value = "/manager", params = "empId", method = RequestMethod.POST)
    public BaseResponse runTimeRecordManagerForEmp(@RequestParam Integer empId) {
        int recordsSaved = timeRecordManager.ensureRecords(empId);
        return new TimeRecordManagerResponse(empId, recordsSaved);
    }
}
