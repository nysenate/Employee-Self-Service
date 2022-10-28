package gov.nysenate.ess.core.service.period;

import com.google.common.collect.*;
import gov.nysenate.ess.core.dao.period.PayPeriodDao;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodNotFoundEx;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.util.SortOrder;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

@Service
public class EssPayPeriodService implements PayPeriodService {
    private final PayPeriodDao payPeriodDao;
    private EnumMap<PayPeriodType, RangeMap<LocalDate, PayPeriod>> typeMap = new EnumMap<>(PayPeriodType.class);

    @Autowired
    public EssPayPeriodService(PayPeriodDao payPeriodDao) {
        this.payPeriodDao = payPeriodDao;
    }

    /** --- Pay Period Service Implemented Methods --- */

    @Override
    public PayPeriod getPayPeriod(PayPeriodType type, LocalDate date) throws PayPeriodNotFoundEx {
        PayPeriod result = getPeriodMap(type).get(date);
        if (result == null) {
            throw new PayPeriodNotFoundEx("Pay period containing date " + date + " could not be found.");
        }
        return result;
    }

    @Override
    public List<PayPeriod> getPayPeriods(PayPeriodType type, Range<LocalDate> dateRange, SortOrder dateOrder) {
        RangeMap<LocalDate, PayPeriod> subMap = getPeriodMap(type).subRangeMap(dateRange);
        Map<Range<LocalDate>, PayPeriod> normalMap = dateOrder == SortOrder.DESC ?
                subMap.asDescendingMapOfRanges() : subMap.asMapOfRanges();
        return new ArrayList<>(normalMap.values());
    }

    private RangeMap<LocalDate, PayPeriod> getPeriodMap(PayPeriodType type) {
        RangeMap<LocalDate, PayPeriod> cachedMap = typeMap.get(type);
        if (cachedMap != null) {
            return cachedMap;
        }
        final RangeMap<LocalDate, PayPeriod> rangeMap = TreeRangeMap.create();
        Range<LocalDate> cacheRange = Range.upTo(LocalDate.now().plusYears(2), BoundType.CLOSED);
        payPeriodDao.getPayPeriods(type, cacheRange, SortOrder.ASC)
                .forEach(p -> rangeMap.put(Range.closed(p.getStartDate(), p.getEndDate()), p));
        typeMap.put(type, rangeMap);
        return rangeMap;
    }

    @Scheduled(cron = "${cache.cron.period}")
    private void initializeData() {
        typeMap.clear();
        for (var type : PayPeriodType.values()) {
            getPeriodMap(type);
        }
    }
}
