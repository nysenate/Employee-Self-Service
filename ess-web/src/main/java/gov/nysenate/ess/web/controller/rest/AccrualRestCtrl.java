package gov.nysenate.ess.web.controller.rest;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.web.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.web.client.view.AccrualsView;
import gov.nysenate.ess.core.dao.period.PayPeriodDao;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.web.client.response.base.BaseResponse;
import gov.nysenate.ess.web.client.response.base.ListViewResponse;
import gov.nysenate.ess.web.client.response.error.ErrorCode;
import gov.nysenate.ess.web.client.response.error.ErrorResponse;
import gov.nysenate.ess.seta.model.accrual.AccrualException;
import gov.nysenate.ess.seta.model.accrual.PeriodAccSummary;
import gov.nysenate.ess.core.model.period.PayPeriodException;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.web.service.accrual.AccrualComputeService;
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

@RestController
@RequestMapping(BaseRestCtrl.REST_PATH + "/accruals")
public class AccrualRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(AccrualRestCtrl.class);

    @Autowired private AccrualComputeService accrualService;
    @Autowired private PayPeriodDao payPeriodDao;

    @RequestMapping("")
    public BaseResponse getAccruals(@RequestParam int empId, @RequestParam String beforeDate) {
        LocalDate beforeLocalDate = parseISODate(beforeDate, "pay period");
        try {
            PayPeriod payPeriod = payPeriodDao.getPayPeriod(PayPeriodType.AF, beforeLocalDate.minusDays(1));
            PeriodAccSummary periodAccSummary = accrualService.getAccruals(empId, payPeriod);
            return new ViewObjectResponse<>(new AccrualsView(periodAccSummary));
        }
        catch (PayPeriodException e) {
            logger.error("Failed to find pay period before {}", beforeLocalDate, e);
        }
        catch (AccrualException e) {
            logger.error("Failed to obtain accruals for employee {}", empId, e);
        }
        return new ErrorResponse(ErrorCode.APPLICATION_ERROR);
    }

    @RequestMapping("/history")
    public BaseResponse getAccruals(@RequestParam int empId, @RequestParam String fromDate, @RequestParam String toDate) {
        LocalDate fromLocalDate = parseISODate(fromDate, "from date");
        LocalDate toLocalDate = parseISODate(toDate, "to date");
        try {
            List<PayPeriod> periods =
                payPeriodDao.getPayPeriods(PayPeriodType.AF, Range.closed(fromLocalDate, toLocalDate), SortOrder.ASC);
            TreeMap<PayPeriod, PeriodAccSummary> accruals = accrualService.getAccruals(empId, periods);
            return ListViewResponse.of(accruals.values().stream().map(AccrualsView::new).collect(Collectors.toList()));
        }
        catch (AccrualException e) {
            logger.error("Failed to obtain accruals for employee {}", empId, e);
        }
        return new ErrorResponse(ErrorCode.APPLICATION_ERROR);
    }
}
