package gov.nysenate.ess.core.service.period;

import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.period.HolidayDao;
import gov.nysenate.ess.core.model.period.Holiday;
import gov.nysenate.ess.core.util.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EssHolidayService implements HolidayService {
    private final HolidayDao holidayDao;
    private ImmutableSortedMap<LocalDate, Holiday> holidayTreeMap;

    @Autowired
    public EssHolidayService(HolidayDao holidayDao) {
        this.holidayDao = holidayDao;
        initializeData();
    }

    @Override
    public Optional<Holiday> getActiveHoliday(LocalDate date) {
        return Optional.ofNullable(holidayTreeMap.get(date))
                .filter(holiday -> !holiday.isQuestionable());
    }

    @Override
    public List<Holiday> getHolidays(LocalDate fromDate, LocalDate toDate, boolean includeQuestionable) {
        var submap = holidayTreeMap.subMap(fromDate, true, toDate, true);
        List<Holiday> holidays = new ArrayList<>(submap.values());
        if (!includeQuestionable) {
            holidays = holidays.stream().filter(h -> !h.isQuestionable()).collect(Collectors.toList());
        }
        return holidays;
    }

    @Scheduled(cron = "${cache.cron.holiday}")
    private void initializeData() {
        var range = Range.upTo(LocalDate.now().plusYears(2), BoundType.CLOSED);
        holidayTreeMap = holidayDao.getHolidays(range, true, SortOrder.ASC).stream()
                .collect(ImmutableSortedMap.toImmutableSortedMap(Comparator.naturalOrder(), Holiday::getDate, Function.identity()));
    }
}
