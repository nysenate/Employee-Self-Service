package gov.nysenate.ess.seta.service.attendance.validation;

import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.seta.model.attendance.TimeEntry;
import gov.nysenate.ess.seta.model.attendance.TimeRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * This validator ensures that new time records are not saved by users
 * and that saved time records do not contain illegal modifications
 */
@Service
public class PermittedModificationTRV implements TimeRecordValidator
{
    private static final Logger logger = LoggerFactory.getLogger(PermittedModificationTRV.class);

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState) {
        return true;
    }

    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState) throws TimeRecordErrorException {
        if (!previousState.isPresent()) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.NO_EXISTING_RECORD);
        }
        TimeRecord prevRecord = previousState.get();
        // Check various user-immutable fields to ensure there are no changeS
        checkTimeRecordField(record, prevRecord, "timeRecordId", "String", TimeRecord::getTimeRecordId);
        checkTimeRecordField(record, prevRecord, "employeeId", "integer", TimeRecord::getEmployeeId);
        checkTimeRecordField(record, prevRecord, "supervisorId", "integer", TimeRecord::getSupervisorId);
        checkTimeRecordField(record, prevRecord, "respHeadCode", "String", TimeRecord::getRespHeadCode);
        checkTimeRecordField(record, prevRecord, "active", "boolean", TimeRecord::isActive);
        checkTimeRecordField(record, prevRecord, "payPeriod", "PayPeriod", TimeRecord::getPayPeriod);
        checkTimeRecordField(record, prevRecord, "beginDate", "Date", TimeRecord::getBeginDate);
        checkTimeRecordField(record, prevRecord, "endDate", "Date", TimeRecord::getEndDate);
        checkTimeRecordField(record, prevRecord, "exceptionDetails", "String", TimeRecord::getExceptionDetails);
        checkTimeRecordField(record, prevRecord, "processedDate", "Date", TimeRecord::getProcessedDate);
        checkTimeEntries(record, prevRecord);
    }

    /** --- Internal Methods --- */

    /**
     * Checks the two field values to ensure they are equal
     * @throws TimeRecordErrorException expressing the fields' immutability if they are not equal
     */
    private static void checkField(Object newVal, Object prevVal, String fieldName, String fieldType)
            throws TimeRecordErrorException {
        if (!Objects.equals(prevVal, newVal)) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.UNAUTHORIZED_MODIFICATION,
                    new InvalidParameterView(fieldName, fieldType,
                            fieldName + " is immutable to users", String.valueOf(newVal)));
        }
    }

    /**
     * Checks that the two given time records contain equal values for a particular field
     * @see #checkField(Object, Object, String, String)
     */
    private static void checkTimeRecordField(TimeRecord newRecord, TimeRecord prevRecord,
                                             String fieldName, String fieldType, Function<TimeRecord, ?> fieldGetter)
            throws TimeRecordErrorException {
        checkField(fieldGetter.apply(newRecord), fieldGetter.apply(prevRecord), fieldName, fieldType);
    }

    /**
     * Checks to ensure that a saved record contains no new time entries
     *   and that no changes in pay type were made
     */
    private static void checkTimeEntries(TimeRecord record, TimeRecord prevState) throws TimeRecordErrorException {
        for (TimeEntry entry : record.getTimeEntries()) {
            TimeEntry prevEntry = prevState.getEntry(entry.getDate());
            if (prevEntry == null) {
                throw new TimeRecordErrorException(TimeRecordErrorCode.UNAUTHORIZED_MODIFICATION,
                        new InvalidParameterView("timeEntries", "List<TimeEntry>", "new entries cannot be added",
                                String.valueOf(entry.getDate())));
            }
            checkField(entry.getPayType(), prevEntry.getPayType(), "payType", "PayType");
        }
    }
}
