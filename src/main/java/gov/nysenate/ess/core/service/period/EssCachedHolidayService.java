package gov.nysenate.ess.core.service.period;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.period.HolidayDao;
import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.model.period.Holiday;
import gov.nysenate.ess.core.service.base.CachingService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EssCachedHolidayService extends CachingService<String, EssCachedHolidayService.HolidayCacheTree>
        implements HolidayService {
    private static final String HOLIDAY_CACHE_KEY = "holiday";

    private final HolidayDao holidayDao;

    @Autowired
    public EssCachedHolidayService(HolidayDao holidayDao) {
        this.holidayDao = holidayDao;
    }

    static final class HolidayCacheTree {
        private final TreeMap<LocalDate, Holiday> holidayTreeMap = new TreeMap<>();

        public HolidayCacheTree(List<Holiday> holidays) {
            holidays.forEach(h -> holidayTreeMap.put(h.getDate(), h));
        }

        public Optional<Holiday> getHoliday(LocalDate date) {
            return Optional.ofNullable(holidayTreeMap.get(date));
        }

        public List<Holiday> getHolidays(Range<LocalDate> dateRange) {
            return new ArrayList<>(holidayTreeMap.subMap(DateUtils.startOfDateRange(dateRange), true,
                                         DateUtils.endOfDateRange(dateRange), true).values());
        }
    }

    @Override
    public Optional<Holiday> getActiveHoliday(LocalDate date) {
        return getHolidayCacheTree()
                .getHoliday(date)
                .filter(holiday -> !holiday.isQuestionable());
    }

    @Override
    public List<Holiday> getHolidays(Range<LocalDate> dateRange, boolean includeQuestionable, SortOrder dateOrder) {
        List<Holiday> holidays = getHolidayCacheTree().getHolidays(dateRange);
        if (!includeQuestionable) {
            holidays = holidays.stream().filter(h -> !h.isQuestionable()).collect(Collectors.toList());
        }
        if (dateOrder.equals(SortOrder.DESC)) {
            Collections.reverse(holidays);
        }
        return holidays;
    }

    /** --- Caching Service Implemented Methods ---
     * @see CachingService*/

    /** {@inheritDoc} */
    @Override
    public CacheType cacheType() {
        return CacheType.HOLIDAY;
    }

    /** --- Internal Methods --- */

    private HolidayCacheTree getHolidayCacheTree() {
        HolidayCacheTree value = cache.get(HOLIDAY_CACHE_KEY);
        if (value == null) {
            cacheHolidays();
            value = cache.get(HOLIDAY_CACHE_KEY);
            if (value == null) {
                throw new IllegalStateException("Holidays are not caching properly.");
            }
        }
        return value;
    }

    @Override
    protected Map<String, HolidayCacheTree> initialEntries() {
        var range = Range.upTo(LocalDate.now().plusYears(2), BoundType.CLOSED);
        var holidays = holidayDao.getHolidays(range, true, SortOrder.ASC);
        return Map.of(HOLIDAY_CACHE_KEY, new HolidayCacheTree(holidays));
    }

    @Scheduled(cron = "${cache.cron.holiday}") // Refresh the cache every 12 hours
    private void cacheHolidays() {
        clearCache(true);
    }
}
