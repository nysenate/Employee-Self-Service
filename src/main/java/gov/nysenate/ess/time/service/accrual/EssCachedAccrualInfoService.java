package gov.nysenate.ess.time.service.accrual;

import com.google.common.collect.*;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.RangeUtils;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.time.dao.attendance.AttendanceDao;
import gov.nysenate.ess.time.model.accrual.AnnualAccSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A service that provides accrual information
 */
@Service
public class EssCachedAccrualInfoService implements AccrualInfoService {
    @Autowired private CachedAnnualAccrualService cacheService;
    @Autowired private AttendanceDao attendanceDao;
    @Autowired private PayPeriodService payPeriodService;
    @Autowired private EmpTransactionService transService;


    /* --- Accrual Info Service Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public ImmutableSortedMap<Integer, AnnualAccSummary> getAnnualAccruals(int empId, int endYear) {
        return cacheService.getAnnualAccruals(empId, endYear);
    }

    /** {@inheritDoc} */
    @Override
    public List<PayPeriod> getActiveAttendancePeriods(int empId, LocalDate endDate, SortOrder dateOrder) {
        ImmutableSortedMap<Integer, AnnualAccSummary> annAcc = getAnnualAccruals(empId, endDate.getYear());
        Optional<Integer> openYear = annAcc.descendingMap().entrySet().stream()
                .filter(e -> e.getValue().getCloseDate() == null)
                .map(Map.Entry::getKey)
                .findFirst();
        if (openYear.isPresent()) {
            return payPeriodService.getPayPeriods(
                PayPeriodType.AF, Range.closed(LocalDate.of(openYear.get(), 1, 1), endDate), dateOrder);
        }
        return new ArrayList<>();
    }

    /** {@inheritDoc} */
    @Override
    public List<PayPeriod> getOpenPayPeriods(PayPeriodType type, Integer empId, SortOrder dateOrder) {
        RangeSet<LocalDate> openDates = attendanceDao.getOpenDates(empId);
        return openDates.isEmpty() ? Collections.emptyList() : payPeriodService.getPayPeriods(type, openDates.span(), dateOrder);
    }

    /** {@inheritDoc} */
    @Override
    public SortedSet<Integer> getAccrualYears(int empId) {
        TransactionHistory transHistory = transService.getTransHistory(empId);

        RangeSet<LocalDate> accrualAllowedDates = transHistory.getAccrualDates();
        RangeSet<LocalDate> employedTimeEntryDates = transHistory.getPerStatusDates(
                perStat -> perStat.isEmployed() & perStat.isTimeEntryRequired());
        RangeSet<LocalDate> annualEmpDates = transHistory.getPayTypeDates(PayType::isBiweekly);
        RangeSet<LocalDate> nonSenatorDates = transHistory.getSenatorDates().complement();
        RangeSet<LocalDate> notFuture = ImmutableRangeSet.of(Range.atMost(LocalDate.now()));

        RangeSet<LocalDate> accrualDates = RangeUtils.intersection(Arrays.asList(
                accrualAllowedDates, employedTimeEntryDates, annualEmpDates, nonSenatorDates, notFuture));

        RangeSet<Integer> yearRanges = TreeRangeSet.create();
        accrualDates.asRanges().stream()
                .map(range -> DateUtils.toYearRange(range, false))
                .forEach(yearRanges::add);

        return yearRanges.asRanges().stream()
                .peek(range -> {
                    if (!(range.hasLowerBound() && range.hasUpperBound())) {
                        throw new IllegalStateException("Accrual state for " + empId + " is unbounded");
                    }
                })
                .map(RangeUtils::getCounter)
                .flatMap(Streams::stream)
                .collect(Collectors.toCollection(TreeSet::new));
    }
}
