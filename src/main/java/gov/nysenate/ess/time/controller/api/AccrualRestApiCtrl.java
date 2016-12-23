package gov.nysenate.ess.time.controller.api;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.time.client.view.AccrualsAvailableView;
import gov.nysenate.ess.time.client.view.AccrualsView;
import gov.nysenate.ess.time.model.accrual.AccrualsAvailable;
import gov.nysenate.ess.time.model.accrual.PeriodAccSummary;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import gov.nysenate.ess.time.service.accrual.AccrualComputeService;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static gov.nysenate.ess.time.model.auth.TimePermissionObject.ACCRUAL;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/accruals")
public class AccrualRestApiCtrl extends BaseRestApiCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(AccrualRestApiCtrl.class);

    @Autowired private AccrualComputeService accrualService;
    @Autowired private PayPeriodService payPeriodService;

    @RequestMapping("")
    public BaseResponse getAccruals(@RequestParam int empId, @RequestParam String beforeDate) {
        LocalDate beforeLocalDate = parseISODate(beforeDate, "pay period");

        checkPermission(new EssTimePermission( empId, ACCRUAL, GET, Range.singleton(beforeLocalDate)));

        PayPeriod payPeriod = payPeriodService.getPayPeriod(PayPeriodType.AF, beforeLocalDate);
        AccrualsAvailable accrualsAvailable = accrualService.getAccrualsAvailable(empId, payPeriod);
        return new ViewObjectResponse<>(new AccrualsAvailableView(accrualsAvailable));
    }

    @RequestMapping("/history")
    public BaseResponse getAccruals(@RequestParam int empId, @RequestParam String fromDate, @RequestParam String toDate) {
        LocalDate fromLocalDate = parseISODate(fromDate, "from date");
        LocalDate toLocalDate = parseISODate(toDate, "to date");

        checkPermission(new EssTimePermission(empId, ACCRUAL, GET, Range.closed(fromLocalDate, toLocalDate)));

        List<PayPeriod> periods =
            payPeriodService.getPayPeriods(PayPeriodType.AF, Range.closed(fromLocalDate, toLocalDate), SortOrder.ASC);
        TreeMap<PayPeriod, PeriodAccSummary> accruals = accrualService.getAccruals(empId, periods);
        return ListViewResponse.of(accruals.values().stream().map(AccrualsView::new).collect(Collectors.toList()));
    }
}
