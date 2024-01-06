package gov.nysenate.ess.core.service.period;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import gov.nysenate.ess.core.dao.period.PayPeriodDao;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodNotFoundEx;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.service.RefreshedCachedData;
import gov.nysenate.ess.core.util.CollectionUtils;
import gov.nysenate.ess.core.util.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EssPayPeriodService
        extends RefreshedCachedData<PayPeriodType, RangeMap<LocalDate, PayPeriod>>
        implements PayPeriodService {
    @Autowired
    public EssPayPeriodService(PayPeriodDao payPeriodDao, @Value("${cache.cron.period}") String cron) {
        super(cron, () -> CollectionUtils.keysToMap(List.of(PayPeriodType.values()),
                type -> getPeriodMap(type, payPeriodDao)));
    }

    /** --- Pay Period Service Implemented Methods --- */

    @Override
    public PayPeriod getPayPeriod(PayPeriodType type, LocalDate date) throws PayPeriodNotFoundEx {
        PayPeriod result = dataMap().get(type).get(date);
        if (result == null) {
            throw new PayPeriodNotFoundEx("Pay period containing date " + date + " could not be found.");
        }
        return result;
    }

    @Override
    public List<PayPeriod> getPayPeriods(PayPeriodType type, Range<LocalDate> dateRange, SortOrder dateOrder) {
        RangeMap<LocalDate, PayPeriod> subMap = dataMap().get(type).subRangeMap(dateRange);
        Map<Range<LocalDate>, PayPeriod> normalMap = dateOrder == SortOrder.DESC ?
                subMap.asDescendingMapOfRanges() : subMap.asMapOfRanges();
        return new ArrayList<>(normalMap.values());
    }

    private static RangeMap<LocalDate, PayPeriod> getPeriodMap(PayPeriodType type, PayPeriodDao periodDao) {
        final RangeMap<LocalDate, PayPeriod> rangeMap = TreeRangeMap.create();
        Range<LocalDate> cacheRange = Range.upTo(LocalDate.now().plusYears(2), BoundType.CLOSED);
        periodDao.getPayPeriods(type, cacheRange, SortOrder.ASC)
                .forEach(p -> rangeMap.put(Range.closed(p.getStartDate(), p.getEndDate()), p));
        return rangeMap;
    }
}
