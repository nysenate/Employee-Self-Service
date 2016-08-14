package gov.nysenate.ess.time.controller.api;

import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.time.client.view.PaycheckView;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import gov.nysenate.ess.time.model.payroll.Paycheck;
import gov.nysenate.ess.time.service.payroll.PaycheckService;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static gov.nysenate.ess.time.model.auth.TimePermissionObject.PAYCHECK;
import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/paychecks")
public class PaycheckRestApiCtrl extends BaseRestApiCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(PaycheckRestApiCtrl.class);

    @Autowired
    PaycheckService paycheckService;

    @RequestMapping(value = "", params = {"empId", "year"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse getPaychecksByYear(@RequestParam Integer empId, @RequestParam Integer year, WebRequest webRequest) {
        checkPermission(new EssTimePermission( empId, PAYCHECK, GET, DateUtils.yearDateRange(year)));

        List<Paycheck> paychecks = paycheckService.getEmployeePaychecksForYear(empId, year);
        return ListViewResponse.of(paychecks.stream().map(PaycheckView::new).collect(toList()), "paychecks");
    }

}
