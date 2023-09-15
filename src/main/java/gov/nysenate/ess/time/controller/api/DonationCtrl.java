package gov.nysenate.ess.time.controller.api;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.client.response.error.ErrorResponse;
import gov.nysenate.ess.core.client.view.base.DonationInfoView;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.time.dao.accrual.DonationDao;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import gov.nysenate.ess.time.service.accrual.AccrualComputeService;
import gov.nysenate.ess.time.util.AccrualUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.client.response.error.ErrorCode.ERROR_SUBMITTING_TIME_DONATION;
import static gov.nysenate.ess.time.model.EssTimeConstants.HOURS_PER_DAY;
import static gov.nysenate.ess.time.model.auth.TimePermissionObject.ACCRUAL;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/donation")
public class DonationCtrl extends BaseRestApiCtrl {
    private static final BigDecimal MAX_DAYS_DONATED = new BigDecimal(20),
            MIN_DAYS_REMAINING = new BigDecimal(10), HALF = new BigDecimal("0.5");
    private final DonationDao donationDao;
    private final AccrualComputeService accrualComputeService;
    private final PayPeriodService payPeriodService;
    private final EmployeeInfoService employeeInfoService;
    private final EmpTransactionService empTransService;


    @Autowired
    public DonationCtrl(DonationDao donationDao, AccrualComputeService accrualComputeService,
                        PayPeriodService payPeriodService, EmployeeInfoService employeeInfoService,
                        EmpTransactionService empTransService) {
        this.donationDao = donationDao;
        this.accrualComputeService = accrualComputeService;
        this.payPeriodService = payPeriodService;
        this.employeeInfoService = employeeInfoService;
        this.empTransService = empTransService;
    }

    @GetMapping("/info")
    public BaseResponse getDonationInfo(@RequestParam int empId) {
        checkPermission(new EssTimePermission(empId, ACCRUAL, GET, Range.singleton(LocalDate.now())));
        Employee emp = employeeInfoService.getEmployee(empId);
        PayPeriod payPeriod = payPeriodService.getPayPeriod(PayPeriodType.AF, LocalDate.now());
        BigDecimal accruedSickTime = accrualComputeService.getAccrualsAvailable(empId, payPeriod).getSickAvailable();
        BigDecimal empHoursPerDay = HOURS_PER_DAY.multiply(getProratePercentageFromEmpId(emp));

        var yearlyDonationLimit = empHoursPerDay.multiply(MAX_DAYS_DONATED)
                .subtract(donationDao.getTimeDonatedThisYear(empId));
        var timeRemainingLimit = accruedSickTime.subtract(empHoursPerDay.multiply(MIN_DAYS_REMAINING));
        // Result needs to be rounded to the nearest multiple of 0.5
        BigDecimal unRoundedResult = timeRemainingLimit.min(yearlyDonationLimit).max(BigDecimal.ZERO);
        BigDecimal decimalPart = unRoundedResult.remainder(BigDecimal.ONE);
        BigDecimal roundedResult = new BigDecimal(unRoundedResult.intValue());
        if (decimalPart.compareTo(HALF) >= 0) {
            roundedResult = roundedResult.add(HALF);
        }
        return new ViewObjectResponse<>(new DonationInfoView(roundedResult, accruedSickTime));
    }

    @GetMapping("/history")
    // TODO: order sick time donations within days
    public BaseResponse getDonationHistory(@RequestParam int empId, @RequestParam int year) {
        checkPermission(new EssTimePermission(empId, ACCRUAL, GET, Range.singleton(LocalDate.now())));
        var history = donationDao.getDonatedTime(empId, year)
                // Data should be ordered by date.
                .asMap().entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByKey()))
                .map(entry -> donationString(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return new ViewObjectResponse<>(ListView.ofStringList(history));
    }

    @PostMapping("/submit")
    public BaseResponse submitDonation(@RequestParam int empId, @RequestParam String hoursToDonate) {
        checkPermission(new EssTimePermission(empId, ACCRUAL, POST, Range.singleton(LocalDate.now())));
        BigDecimal donation = BigDecimal.valueOf(Double.parseDouble(hoursToDonate));
        Employee emp = employeeInfoService.getEmployee(empId);
        if (donationDao.submitDonation(emp, donation)) {
            return new SimpleResponse(true, "Donation submitted!", "sick-time-donation");
        }
        return new ErrorResponse(ERROR_SUBMITTING_TIME_DONATION);
    }

    private BigDecimal getProratePercentageFromEmpId(Employee emp) {
        if (emp.getPayType() == PayType.RA) {
            return BigDecimal.ONE;
        }
        // Temporary employees cannot donate.
        if (emp.getPayType() == PayType.TE) {
            return BigDecimal.ZERO;
        }
        var transHistory = empTransService.getTransHistory(emp.getEmployeeId());
        BigDecimal minTotalHours = transHistory.getEffectiveMinHours(Range.singleton(LocalDate.now()))
                .lastEntry().getValue();
        return AccrualUtils.getProratePercentage(minTotalHours);
    }

    private static String donationString(LocalDate date, Collection<BigDecimal> hours) {
        var stringHours = hours.stream().map(BigDecimal::toString).collect(Collectors.joining(", "));
        return date.getMonthValue() + "/" + date.getDayOfMonth() + ": " + stringHours;
    }
}
