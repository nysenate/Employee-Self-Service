package gov.nysenate.ess.travel.application.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.travel.application.dao.SqlUserConfigDao;
import gov.nysenate.ess.travel.application.model.EmployeeRequestorInfo;
import gov.nysenate.ess.travel.application.view.EmployeeRequestorView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;


@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel/user/config")
public class TravelUserConfigCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(TravelUserConfigCtrl.class);

    @Autowired private SqlUserConfigDao sqlUserConfigDao;

    @RequestMapping(value = "", method = {GET, RequestMethod.HEAD}, params = "empId")
    public BaseResponse getConfig(@RequestParam(required = true) Integer empId){
        EmployeeRequestorInfo info = sqlUserConfigDao.getRequestorInfoById(empId);
        EmployeeRequestorView employeeRequestorView = new EmployeeRequestorView(info);

        return new ViewObjectResponse<>(employeeRequestorView);
    }
}
