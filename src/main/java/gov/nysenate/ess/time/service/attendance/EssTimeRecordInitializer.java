package gov.nysenate.ess.time.service.attendance;

import com.google.common.collect.RangeMap;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.period.Holiday;
import gov.nysenate.ess.core.model.transaction.TransactionHistory;
import gov.nysenate.ess.core.service.period.HolidayService;
import gov.nysenate.ess.core.service.transaction.EmpTransactionService;
import gov.nysenate.ess.core.util.RangeUtils;
import gov.nysenate.ess.time.model.attendance.TimeEntry;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class EssTimeRecordInitializer implements TimeRecordInitializer {
    private final HolidayService holidayService;
    private final EmpTransactionService transService;

    @Autowired
    public EssTimeRecordInitializer(HolidayService holidayService, EmpTransactionService transService) {
        this.holidayService = holidayService;
        this.transService = transService;
    }

    /**
     * Ensures that the given time record contains entries for each day covered.
     *
     * @param timeRecord - TimeRecord
     */
    @Override
    public void initializeEntries(TimeRecord timeRecord) {
        TransactionHistory transHistory = transService.getTransHistory(timeRecord.getEmployeeId());
        RangeMap<LocalDate, PayType> payTypeMap = RangeUtils.toRangeMap(
                transHistory.getEffectivePayTypes(timeRecord.getDateRange()), timeRecord.getEndDate());

        for (LocalDate entryDate = timeRecord.getBeginDate();
             !entryDate.isAfter(timeRecord.getEndDate());
             entryDate = entryDate.plusDays(1)) {

            Optional<Holiday> holiday = holidayService.getActiveHoliday(entryDate);
            TimeEntry entry = timeRecord.getEntry(entryDate);
            if (entry == null) {
                // Entry does not yet exist, fully initialize it.
                entry = new TimeEntry(timeRecord, payTypeMap.get(entryDate), entryDate);
                initHolidayHours(entry, holiday);
                timeRecord.addTimeEntry(entry);
            } else if (holiday.isPresent()) {
                // Initialize holidays if not yet overwritten by the user or if used holiday hours is > what is provided.
                if (entry.getHolidayHours().isEmpty() || entry.getHolidayHours().get().compareTo(holiday.get().getHours()) > 0) {
                    initHolidayHours(entry, holiday);
                }
            }
            // Otherwise do nothing (The TimeEntry exists and entryDate is not a holiday)
        }
    }

    private void initHolidayHours(TimeEntry entry, Optional<Holiday> holiday) {
        if (holiday.isPresent()) {
            switch (entry.getPayType()) {
                case RA -> entry.setHolidayHours(holiday.get().getHours());
                case SA, SE -> entry.setHolidayHours(BigDecimal.ZERO);
                case TE -> entry.setHolidayHours(null);
            }
        }
    }
}
