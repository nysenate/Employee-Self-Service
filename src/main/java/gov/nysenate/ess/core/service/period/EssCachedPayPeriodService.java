package gov.nysenate.ess.core.service.period;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import gov.nysenate.ess.core.dao.period.PayPeriodDao;
import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodNotFoundEx;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.service.base.CachingService;
import gov.nysenate.ess.core.util.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class EssCachedPayPeriodService extends CachingService<PayPeriodType, EssCachedPayPeriodService.PayPeriodCacheTree>
        implements PayPeriodService {
    private static final Logger logger = LoggerFactory.getLogger(EssCachedPayPeriodService.class);
    private final PayPeriodDao payPeriodDao;

    @Autowired
    public EssCachedPayPeriodService(PayPeriodDao payPeriodDao) {
        this.payPeriodDao = payPeriodDao;
        if (warmOnStartup) {
            cacheAfPayPeriods();
        }
    }

    /**
     * Since we lookup pay periods by determining if the given date intersects the period's date range,
     * we need to store the periods in a tree map to avoid having to loop through each one to find the
     * one we want.
     */
    static class PayPeriodCacheTree {
        private final RangeMap<LocalDate, PayPeriod> rangeMap = TreeRangeMap.create();

        public PayPeriodCacheTree(TreeSet<PayPeriod> periodSet) {
            periodSet.forEach(p -> rangeMap.put(Range.closed(p.getStartDate(), p.getEndDate()), p));
        }

        public PayPeriod getPayPeriod(LocalDate date) {
            PayPeriod period = rangeMap.get(date);
            if (period == null) throw new PayPeriodNotFoundEx("Pay period containing date " + date + " could not be found.");
            return period;
        }

        // TODO: wrong cuz return value isn't used?
        public List<PayPeriod> getPayPeriodsInRange(Range<LocalDate> dateRange, SortOrder dateOrder) {
            List<PayPeriod> payPeriods = new ArrayList<>(rangeMap.subRangeMap(dateRange).asMapOfRanges().values());
            if (dateOrder.equals(SortOrder.DESC)) {
                Collections.reverse(payPeriods);
            }
            return payPeriods;
        }
    }

    /** --- Pay Period Service Implemented Methods --- */

    @Override
    public PayPeriod getPayPeriod(PayPeriodType type, LocalDate date) throws PayPeriodNotFoundEx {
        if (type.equals(PayPeriodType.AF)) {
            PayPeriodCacheTree cacheTree = getCachedPayPeriodTree(type);
            cacheTree.getPayPeriod(date);
        }
        return payPeriodDao.getPayPeriod(type, date);
    }

    @Override
    public List<PayPeriod> getPayPeriods(PayPeriodType type, Range<LocalDate> dateRange, SortOrder dateOrder) {
        if (type.equals(PayPeriodType.AF)) {
            PayPeriodCacheTree cacheTree = getCachedPayPeriodTree(type);
            cacheTree.getPayPeriodsInRange(dateRange, dateOrder);
        }
        return payPeriodDao.getPayPeriods(type, dateRange, dateOrder);
    }

    /** --- Caching Service Implemented Methods ---
     * @see CachingService */

    /** {@inheritDoc} */
    @Override
    public CacheType cacheType() {
        return CacheType.PAY_PERIOD;
    }

    /** --- Internal Methods --- */

    private PayPeriodCacheTree getCachedPayPeriodTree(PayPeriodType type) {
        PayPeriodCacheTree value = cache.get(type);
        if (value == null) {
            cache.put(type, getPeriodTree(type));
            // Try again.
            value = cache.get(type);
            if (value == null) {
                throw new IllegalStateException(type + " Pay Periods are not caching properly.");
            }
        }
        return value;
    }

    @Override
    protected Map<PayPeriodType, PayPeriodCacheTree> initialEntries() {
        return Map.of(PayPeriodType.AF, getPeriodTree(PayPeriodType.AF));
    }

    @Scheduled(cron = "${cache.cron.period}")
    private void cacheAfPayPeriods() {
        cache.putAll(initialEntries());
    }

    private PayPeriodCacheTree getPeriodTree(PayPeriodType type) {
        Range<LocalDate> cacheRange = Range.upTo(LocalDate.now().plusYears(2), BoundType.CLOSED);
        TreeSet<PayPeriod> payPeriods =
            new TreeSet<>(payPeriodDao.getPayPeriods(type, cacheRange, SortOrder.ASC));
        return new PayPeriodCacheTree(payPeriods);
    }
}