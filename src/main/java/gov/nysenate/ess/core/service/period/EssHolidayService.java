package gov.nysenate.ess.core.service.period;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.period.HolidayDao;
import gov.nysenate.ess.core.model.period.Holiday;
import gov.nysenate.ess.core.service.RefreshedCachedData;
import gov.nysenate.ess.core.util.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EssHolidayService
        extends RefreshedCachedData<LocalDate, Holiday>
        implements HolidayService {
    private final HolidayDao holidayDao;

    @Autowired
    public EssHolidayService(HolidayDao holidayDao, @Value("${cache.cron.holiday}") String cron) {
        super(cron);
        this.holidayDao = holidayDao;
    }

    @Override
    protected Map<LocalDate, Holiday> getMap() {
        var range = Range.upTo(LocalDate.now().plusYears(2), BoundType.CLOSED);
        return toMap(holidayDao.getHolidays(range, true, SortOrder.ASC), Holiday::getDate);
    }

    @Override
    public Optional<Holiday> getActiveHoliday(LocalDate date) {
        return Optional.ofNullable(dataMap().get(date))
                .filter(holiday -> !holiday.isQuestionable());
    }

    @Override
    public List<Holiday> getHolidays(LocalDate fromDate, LocalDate toDate, boolean includeQuestionable) {
        Range<LocalDate> range = Range.closed(fromDate, toDate);
        return dataMap().entrySet().stream()
                .filter(entry -> range.contains(entry.getKey()) && (includeQuestionable || !entry.getValue().isQuestionable()))
                .map(Map.Entry::getValue).collect(Collectors.toList());
    }
}
