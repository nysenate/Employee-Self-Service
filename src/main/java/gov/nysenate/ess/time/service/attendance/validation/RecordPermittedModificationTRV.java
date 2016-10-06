package gov.nysenate.ess.time.service.attendance.validation;

import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.time.model.attendance.TimeEntry;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
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
public class RecordPermittedModificationTRV implements TimeRecordValidator
{
    private static final Logger logger = LoggerFactory.getLogger(RecordPermittedModificationTRV.class);

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) {
        return true;
    }

    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action)
            throws TimeRecordErrorException {
        if (!previousState.isPresent()) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.NO_EXISTING_RECORD);
        }
        TimeRecord prevRecord = previousState.get();
        // Check various user-immutable fields to ensure there are no changes
        checkTimeRecordField(record, prevRecord, "recordStatus", "String", TimeRecord::getRecordStatus);
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
       // checkTimeEntries(record, prevRecord);  -- Commented Out 8/16/16 to test ChangeTRV
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
}
