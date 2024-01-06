package gov.nysenate.ess.time.service.attendance.validation.recordvalidators;

import com.google.common.collect.ContiguousSet;
import gov.nysenate.ess.core.client.view.base.DateView;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.time.client.view.attendance.InvalidTimeEntryParameterView;
import gov.nysenate.ess.time.model.attendance.TimeEntry;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorCode;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorException;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;


/**
 * Checks time entries to ensure that certain time entry fields are not modified
 */
@Service
public class EntryPermittedModificationTRV implements TimeRecordValidator {

    private static final Logger logger = LoggerFactory.getLogger(EntryPermittedModificationTRV.class);

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState,TimeRecordAction action) {
        // Always applicable
        return true;
    }

    /**
     * Check all time entries to ensure that only permitted fields were modified
     *
     * @param record TimeRecord - A posted time record in the process of validation
     * @param previousState TimeRecord - The most recently saved version of the posted time record
     * @throws TimeRecordErrorException if invalid fields were modified
     **/
    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) throws TimeRecordErrorException {
        if (previousState.isEmpty()) {
            return;
        }
        TimeRecord prevState = previousState.get();

        // Get a set of all dates in pay period
        ContiguousSet<LocalDate> recordDates =
                ContiguousSet.create(prevState.getDateRange(), DateUtils.getLocalDateDiscreteDomain());

        // Iterate through each possible date
        for (LocalDate date : recordDates) {
            TimeEntry newEntry = record.getEntry(date);
            TimeEntry prevEntry = prevState.getEntry(date);

            // If neither entry is present, move on
            if (newEntry == null && prevEntry == null) {
                continue;
            }

            // Check for date discrepancies
            checkForDateDiscrepancy(newEntry, prevEntry);

            // Check fields
            checkEntryFields(newEntry, prevEntry);
        }

    }

    /* --- Internal Methods --- */

    /**
     * Raises exception if one entry is missing but the other isn't (from same date)
     */
    private static void checkForDateDiscrepancy(TimeEntry newEntry, TimeEntry prevEntry) {
        if (newEntry == null ^ prevEntry == null) {
            LocalDate existingDate = Optional.ofNullable(newEntry)
                    .orElse(prevEntry)
                    .getDate();
            throw new TimeRecordErrorException(TimeRecordErrorCode.TIME_ENTRY_DATE_DISCREPANCY,
                    new DateView(existingDate));
        }
    }

    /**
     * Check fields for equality between new and previous entries
     */
    private static void checkEntryFields(TimeEntry newEntry, TimeEntry prevEntry) {
        checkEntryField(newEntry, prevEntry, "entryId", "BigInteger", TimeEntry::getEntryId);
        checkEntryField(newEntry, prevEntry, "timeRecordId", "BigInteger", TimeEntry::getTimeRecordId);
        checkEntryField(newEntry, prevEntry, "empId", "BigInteger", TimeEntry::getEmpId);
        checkEntryField(newEntry, prevEntry, "active", "boolean", TimeEntry::isActive);
        checkEntryField(newEntry, prevEntry, "payType", "String", TimeEntry::getPayType);
        checkEntryField(newEntry, prevEntry, "employeeName", "String", TimeEntry::getEmployeeName);
    }

    /**
     * Checks the two time entry field values to ensure they are equal
     * @throws TimeRecordErrorException expressing the fields' immutability if they are not equal
     */
    private static void checkEntryField(TimeEntry newEntry, TimeEntry prevEntry,
                                        String fieldName, String fieldType, Function<TimeEntry, ?> fieldGetter)
            throws TimeRecordErrorException {
        Object newVal = fieldGetter.apply(newEntry);
        Object prevVal = fieldGetter.apply(prevEntry);
        if (!Objects.equals(prevVal, newVal)) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.UNAUTHORIZED_ENTRY_MODIFICATION,
                    new InvalidTimeEntryParameterView(prevEntry, fieldName, fieldType,
                            fieldName + " is immutable to users", String.valueOf(newVal)));
        }
    }
}
