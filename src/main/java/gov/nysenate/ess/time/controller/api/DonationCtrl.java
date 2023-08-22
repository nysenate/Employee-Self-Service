package gov.nysenate.ess.time.controller.api;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.view.base.BigDecimalView;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.time.dao.accrual.DonationDao;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import gov.nysenate.ess.time.service.accrual.AccrualComputeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static gov.nysenate.ess.time.model.auth.TimePermissionObject.ACCRUAL;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/donation")
public class DonationCtrl extends BaseRestApiCtrl {
    // TODO: both to be pro-rated
    private static final BigDecimal MAX_YEARLY_DONATION = new BigDecimal(140),
            MIN_TIME_REMAINING = new BigDecimal(70);
    private final DonationDao donationDao;
    private final AccrualComputeService accrualComputeService;
    private final PayPeriodService payPeriodService;

    @Autowired
    public DonationCtrl(DonationDao donationDao, AccrualComputeService accrualComputeService,
                        PayPeriodService payPeriodService) {
        this.donationDao = donationDao;
        this.accrualComputeService = accrualComputeService;
        this.payPeriodService = payPeriodService;
    }

    @GetMapping("/maxDonation")
    public BaseResponse getMaxDonation(@RequestParam int empId, @RequestParam String effectiveDate) {
        checkPermission(new EssTimePermission(empId, ACCRUAL, GET, Range.singleton(LocalDate.now())));
        LocalDate date = LocalDate.parse(effectiveDate.split("T")[0]);
        var yearlyDonationLimit = MAX_YEARLY_DONATION.
                subtract(donationDao.getTimeDonatedInLastYear(empId, date));
        PayPeriod payPeriod = payPeriodService.getPayPeriod(PayPeriodType.AF, date);
        var timeRemainingLimit = accrualComputeService.getAccrualsAvailable(empId, payPeriod)
                .getSickAvailable().subtract(MIN_TIME_REMAINING);
        var result = timeRemainingLimit.min(yearlyDonationLimit).max(BigDecimal.ZERO);
        return new ViewObjectResponse<>(new BigDecimalView(result));
    }

    @GetMapping("/history")
    public BaseResponse getDonationHistory(@RequestParam int empId, @RequestParam int year) {
        var history = donationDao.getDonatedTime(empId, year)
                // Strings should be ordered by date.
                .asMap().entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(entry -> donationString(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return new ViewObjectResponse<>(ListView.ofStringList(history));
    }

    @PostMapping("/submit")
    // TODO: finish
    public BaseResponse submitDonation(@RequestParam int empId, @RequestParam String effectiveDate, @RequestParam String donation) {
        checkPermission(new EssTimePermission(empId, ACCRUAL, POST, Range.singleton(LocalDate.now())));
        return null;
    }

    private static String donationString(LocalDate date, Collection<BigDecimal> hours) {
        var stringHours = hours.stream().map(BigDecimal::toString).collect(Collectors.joining(", "));
        return date.getMonthValue() + "/" + date.getDayOfMonth() + ": " + stringHours;
    }
}
