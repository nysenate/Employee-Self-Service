package gov.nysenate.ess.time.controller.api;

import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.time.client.view.payroll.PaycheckView;
import gov.nysenate.ess.time.client.view.payroll.PaychecksSummaryView;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import gov.nysenate.ess.time.model.payroll.Paycheck;
import gov.nysenate.ess.time.model.payroll.PaychecksSummary;
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

import java.util.List;

import static gov.nysenate.ess.time.model.auth.TimePermissionObject.PAYCHECK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/paychecks")
public class PaycheckRestApiCtrl extends BaseRestApiCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(PaycheckRestApiCtrl.class);

    @Autowired
    PaycheckService paycheckService;

    /**
     * Paycheck API
     * ------------
     * Get a list of paychecks across a year for an employee
     *
     * Usage:       (GET) /api/v1/paychecks
     *
     * Request Params:
     * @param empId Integer - required - the requested employee id
     * @param year Integer - required - the year for which paychecks will be retrieved
     * @param fiscalYear boolean - default false - will interpret <code>year</code> as a fiscal year if true
     * @return {@link ListViewResponse} of {@link PaycheckView}
     */
    @RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse getPaychecksByYear(@RequestParam Integer empId,
                                           @RequestParam Integer year,
                                           @RequestParam(defaultValue = "false") boolean fiscalYear) {
        checkPermission(new EssTimePermission( empId, PAYCHECK, GET, DateUtils.yearDateRange(year)));

        List<Paycheck> paychecks;

        if (fiscalYear) {
            paychecks = paycheckService.getEmployeePaychecksForFiscalYear(empId, year);
        } else {
            paychecks = paycheckService.getEmployeePaychecksForYear(empId, year);
        }

        PaychecksSummary summary = new PaychecksSummary(paychecks);
        return new ViewObjectResponse<>(new PaychecksSummaryView(summary));
    }

}
