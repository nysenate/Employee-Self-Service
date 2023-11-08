package gov.nysenate.ess.time.service.attendance.validation.recordvalidators;

import gov.nysenate.ess.time.client.view.attendance.SimpleTimeRecordView;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
import gov.nysenate.ess.time.model.attendance.TimeRecordScope;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorCode;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorException;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;


/**
 * Ensures that supervisor and personnel scoped records are not modified upon submission
 * These groups should not be making any modifications to a time record since Approving/Rejecting
 * a record is done via {@link TimeRecordAction}
 */
@Service
public class ScopePermissionTRV implements TimeRecordValidator {

    private static final Logger logger = LoggerFactory.getLogger(ScopePermissionTRV.class);

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) {
        // If the saved record is in a scope other than employee scope
        return record.getScope() != TimeRecordScope.EMPLOYEE;
    }

    /**
     * Ensures that the submitted record is exactly the same as the previous record
     * With the exception of the 'remarks' field.
     *
     * @param record TimeRecord - A posted time record in the process of validation
     * @param prevState Optional<TimeRecord> - The most recently saved version of the posted time record
     * @throws TimeRecordErrorException - if the submitted record differs from the previous record
     */
    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> prevState, TimeRecordAction action) throws TimeRecordErrorException {
        if (prevState.isEmpty()) {
            return;
        }
        TimeRecord prevRecord = prevState.get();

        // Generate a stand-in comparison record for the posted record
        // This record is identical to the posted record with the exception of remarks,
        // which are set to the value of the previous record
        TimeRecord comparisonRecord = new TimeRecord(record);
        comparisonRecord.setRemarks(prevRecord.getRemarks());

        if (!Objects.equals(comparisonRecord, prevRecord)) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.NON_EMPLOYEE_MODIFICATION,
                    new SimpleTimeRecordView(record));
        }
    }
}
