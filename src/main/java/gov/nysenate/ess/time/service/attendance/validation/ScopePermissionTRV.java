package gov.nysenate.ess.time.service.attendance.validation;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.time.model.attendance.TimeEntry;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
import gov.nysenate.ess.time.model.attendance.TimeRecordScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


/**
 * Checks time records to make sure that no time record contains partially entered miscellaneous values
 */

@Service
public class ScopePermissionTRV implements TimeRecordValidator {

    private static final Logger logger = LoggerFactory.getLogger(ScopePermissionTRV.class);

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) {
        // If the saved record contains entries where the employee was a temporary employee
        return record.getScope() != TimeRecordScope.EMPLOYEE;
    }

    /**
     *  checkTimeRecord check hourly increments for all of the daily records
     *
     * @param record TimeRecord - A posted time record in the process of validation
     * @param prevState Optional<TimeRecord> - The most recently saved version of the posted time record
     * @throws TimeRecordErrorException
     */

    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> prevState, TimeRecordAction action) throws TimeRecordErrorException {
        ImmutableList<TimeEntry> entries =  record.getTimeEntries();
        int x = -1;
        TimeRecord previousState = prevState.orElse(null);

        logger.info("checkTimeRecord start");

        if (record.getScope() != TimeRecordScope.EMPLOYEE) {
            logger.info("checkTimeRecord NOT EMPLOYEE RECORD SCOPE, SO CHECKING");
            for (TimeEntry entry : entries) {
                checkEntryFieldPermission(record, entry, getPreviousEntry(entry, previousState));
            }
        }
        else {
            logger.info("checkTimeRecord EMPLOYEE RECORD SCOPE, SO DO NOTHING");
        }
    }

    /**
     * checkTotal:  check row totals
     *
     * @param entry
     * @throws TimeRecordErrorException
     */

    private void checkEntryFieldPermission(TimeRecord record, TimeEntry entry,  TimeEntry previousEntry)  throws TimeRecordErrorException {
        if (record.getScope() != TimeRecordScope.EMPLOYEE) {
            anyFieldChanged(entry, previousEntry);
        }
    }

    private TimeEntry getPreviousEntry (TimeEntry entry,  TimeRecord previousState) {
        ImmutableList<TimeEntry> prevEntries =  previousState.getTimeEntries();

        for (TimeEntry prevEntry : prevEntries) {
            if (    prevEntry != null &&
                    prevEntry.getEntryId() != null &&
                    entry != null &&
                    entry.getEntryId() != null) {
                if (prevEntry.getEntryId().equals(entry.getEntryId())) {
                    return prevEntry;
                }
            }
            else if (prevEntry == null) {
                // TESTING

            }
            else if  (prevEntry.getEntryId() == null) {
                // TESTING

            }
            else if  (entry == null ) {
                // TESTING

            }
            else if  (entry.getEntryId() == null) {
                // TESTING

            }

        }
        return null;
    }

    /**
     * anyFieldChanged: Check to see if any Entry Fields Changed between the previous and the current state.
     * If any fields have changed, throw up an error. This code assumes that it is only being called in a state
     * where Entry Fields do not change.
     *
     * @param entry TimeEntry - Current Entry in current state
     * @param previousEntry TimeEntry - Current Entry in previous state (last saved state)
     * @throws TimeRecordErrorException
     */

    private void anyFieldChanged(TimeEntry entry,  TimeEntry previousEntry) throws TimeRecordErrorException {
        String date;
        BigDecimal currentHours;
        BigDecimal prevHours;

        try {
            date = entry.getDate().format(DateTimeFormatter.ISO_DATE);

        }
        catch (NullPointerException ne) {
            date = "N/A";
        }

        if (entry == null) {
            logger.info("entry is null (anyFieldChanged)");
        }
        else if (previousEntry == null) {
            logger.info("previousEntry is null (anyFieldChanged)");
        }
        else {
            fieldChanged("workHours", entry.getWorkHours().orElse(BigDecimal.ZERO), previousEntry.getWorkHours().orElse(BigDecimal.ZERO), date);
            fieldChanged("travelHours", entry.getTravelHours().orElse(BigDecimal.ZERO), previousEntry.getTravelHours().orElse(BigDecimal.ZERO), date);
            fieldChanged("holidayHours", entry.getHolidayHours().orElse(BigDecimal.ZERO), previousEntry.getHolidayHours().orElse(BigDecimal.ZERO), date);
            fieldChanged("vacationHours", entry.getVacationHours().orElse(BigDecimal.ZERO), previousEntry.getVacationHours().orElse(BigDecimal.ZERO), date);
            fieldChanged("sickEmpHours", entry.getSickEmpHours().orElse(BigDecimal.ZERO), previousEntry.getSickEmpHours().orElse(BigDecimal.ZERO), date);
            fieldChanged("sickFamHours", entry.getSickFamHours().orElse(BigDecimal.ZERO), previousEntry.getSickFamHours().orElse(BigDecimal.ZERO), date);
            fieldChanged("miscHours", entry.getMiscHours().orElse(BigDecimal.ZERO), previousEntry.getMiscHours().orElse(BigDecimal.ZERO), date);
        }

    }

   private void fieldChanged(String fieldName, BigDecimal fieldValue, BigDecimal prevFieldValue, String date) throws TimeRecordErrorException  {
       logger.info("fieldChanged:"+fieldName+" fieldValue:"+fieldValue+", prevFieldValue:"+prevFieldValue);
       if (fieldValue.compareTo(prevFieldValue) != 0) {
           throw new TimeRecordErrorException(TimeRecordErrorCode.ENTRY_CANNOT_CHANGE_IN_SCOPE,
                   new InvalidParameterView(fieldName, "string",
                           fieldName + " = " + prevFieldValue.toString() + " -> " + fieldValue.toString(), date));
       }
   }

}
