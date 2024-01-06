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
     * @param timeRecord - TimeRecord
     */
    @Override
    public void initializeEntries(TimeRecord timeRecord) {
        RangeMap<LocalDate, PayType> payTypeMap = null;

        for (LocalDate entryDate = timeRecord.getBeginDate();
             !entryDate.isAfter(timeRecord.getEndDate());
             entryDate = entryDate.plusDays(1)) {
            if (timeRecord.containsEntry(entryDate)) {
                continue;
            }
            if (payTypeMap == null) {
                TransactionHistory transHistory = transService.getTransHistory(timeRecord.getEmployeeId());
                payTypeMap = RangeUtils.toRangeMap(
                        transHistory.getEffectivePayTypes(timeRecord.getDateRange()), timeRecord.getEndDate());
            }
            var entry = new TimeEntry(timeRecord, payTypeMap.get(entryDate), entryDate);
            timeRecord.addTimeEntry(entry);
            if (entry.getPayType() != PayType.TE) {
                // Set holiday hours if applicable
                Optional<Holiday> holiday = holidayService.getActiveHoliday(entryDate);
                if (holiday.isPresent()) {
                    if (entry.getPayType() == PayType.RA) {
                        entry.setHolidayHours(holiday.get().getHours());
                    }
                    else if (entry.getHolidayHours().isEmpty()) {
                        entry.setHolidayHours(BigDecimal.ZERO);
                    }
                }
            }
        }
    }
}
