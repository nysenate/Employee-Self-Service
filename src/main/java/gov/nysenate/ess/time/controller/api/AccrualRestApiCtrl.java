package gov.nysenate.ess.time.controller.api;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.time.client.view.accrual.AccrualsAvailableView;
import gov.nysenate.ess.time.client.view.accrual.AccrualsView;
import gov.nysenate.ess.time.model.accrual.AccrualsAvailable;
import gov.nysenate.ess.time.model.accrual.PeriodAccSummary;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import gov.nysenate.ess.time.service.accrual.AccrualComputeService;
import gov.nysenate.ess.time.service.accrual.AccrualInfoService;
import gov.nysenate.ess.time.service.accrual.DonationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.ess.time.model.auth.TimePermissionObject.ACCRUAL;
import static gov.nysenate.ess.time.model.auth.TimePermissionObject.ACCRUAL_ACTIVE_YEARS;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/accruals")
public class AccrualRestApiCtrl extends BaseRestApiCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(AccrualRestApiCtrl.class);

    @Autowired private DonationService donationService;
    @Autowired private AccrualComputeService accrualComputeService;
    @Autowired private AccrualInfoService accrualInfoService;
    @Autowired private PayPeriodService payPeriodService;

    @RequestMapping("/active-years")
    public BaseResponse getActiveAccrualYears(@RequestParam int empId) {
        checkPermission(new EssTimePermission(empId, ACCRUAL_ACTIVE_YEARS, GET, Range.singleton(LocalDate.now())));
        SortedSet<Integer> accrualYears = accrualInfoService.getAccrualYears(empId);
        return ListViewResponse.ofIntList(accrualYears, "years");
    }

    @RequestMapping("")
    public BaseResponse getAccruals(@RequestParam int empId, @RequestParam String beforeDate) {
        LocalDate beforeLocalDate = parseISODate(beforeDate, "pay period");
        checkPermission(new EssTimePermission( empId, ACCRUAL, GET, Range.singleton(beforeLocalDate)));

        PayPeriod payPeriod = payPeriodService.getPayPeriod(PayPeriodType.AF, beforeLocalDate);
        AccrualsAvailable accrualsAvailable = accrualComputeService.getAccrualsAvailable(empId, payPeriod);
        return new ViewObjectResponse<>(new AccrualsAvailableView(accrualsAvailable));
    }

    @RequestMapping("/history")
    public BaseResponse getAccruals(@RequestParam int empId, @RequestParam String fromDate, @RequestParam String toDate) {
        LocalDate fromLocalDate = parseISODate(fromDate, "fromDate");
        LocalDate toLocalDate = parseISODate(toDate, "toDate");
        Range<LocalDate> dateRange = getClosedOpenRange(fromLocalDate, toLocalDate, "fromDate", "toDate");
        checkPermission(new EssTimePermission(empId, ACCRUAL, GET, dateRange));

        List<PayPeriod> periods = payPeriodService.getPayPeriods(PayPeriodType.AF, dateRange, SortOrder.ASC);
        TreeMap<PayPeriod, PeriodAccSummary> accruals = accrualComputeService.getAccruals(empId, periods);
        int startYear = accruals.firstKey().getYear();
        Function<Map.Entry<LocalDate, ?>, PayPeriod> getPeriodFunc =
                entry -> payPeriodService.getPayPeriod(PayPeriodType.AF, entry.getKey());
        // Donations are naturally received with a matched LocalDate, but we need PayPeriods.
        SortedMap<PayPeriod, BigDecimal> periodToDonationMap = donationService.
                getHoursDonated(empId, startYear, accruals.lastKey().getEndDate())
                .entrySet().stream()
                .collect(Collectors.toMap(getPeriodFunc, Map.Entry::getValue, BigDecimal::add, TreeMap::new));
        // To make the Map complete, any periods without donations should have a mapping to ZERO.
        accruals.keySet().forEach(period -> periodToDonationMap.computeIfAbsent(period, payPeriod -> BigDecimal.ZERO));

        var views = new ArrayList<AccrualsView>();
        // We need a running total of donations over a year
        int currYear = startYear;
        BigDecimal donationsYtd = BigDecimal.ZERO;
        for (PayPeriod payPeriod : periodToDonationMap.keySet()) {
            if (payPeriod.getYear() != currYear) {
                currYear = payPeriod.getYear();
                donationsYtd = BigDecimal.ZERO;
            }
            BigDecimal periodDonation = periodToDonationMap.get(payPeriod);
            donationsYtd = donationsYtd.add(periodDonation);
            PeriodAccSummary accSumm = accruals.get(payPeriod);
            if (accSumm != null) {
                views.add(new AccrualsView(accruals.get(payPeriod), donationsYtd, periodDonation));
            }
        }
        return ListViewResponse.of(views);
    }
}
