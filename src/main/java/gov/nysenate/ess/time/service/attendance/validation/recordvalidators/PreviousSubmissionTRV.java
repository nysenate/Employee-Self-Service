package gov.nysenate.ess.time.service.attendance.validation.recordvalidators;

import gov.nysenate.ess.time.client.view.attendance.SimpleTimeRecordView;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
import gov.nysenate.ess.time.model.attendance.TimeRecordScope;
import gov.nysenate.ess.time.service.attendance.TimeRecordService;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorCode;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorException;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A validation that is performed when an employee submits a time record to supervisor.
 * Checks to ensure that there are no unsubmitted annual pay time records with earlier dates
 */
@Service
public class PreviousSubmissionTRV implements TimeRecordValidator {

    private final TimeRecordService timeRecordService;

    @Autowired
    public PreviousSubmissionTRV(TimeRecordService timeRecordService) {
        this.timeRecordService = timeRecordService;
    }

    /**
     * {@inheritDoc}
     * Valid when the record is submitted from the employee scope
     */
    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) {
        return record.getScope() == TimeRecordScope.EMPLOYEE && action == TimeRecordAction.SUBMIT;
    }

    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action)
            throws TimeRecordErrorException {
        // Get active (unsubmitted) time records
        // If any of these has a begin date before the posted record's begin date, raise an exception
        timeRecordService.getActiveTimeRecords(record.getEmployeeId()).stream()
                .filter(otherRecord -> otherRecord.getBeginDate().isBefore(record.getBeginDate()))
                .findFirst()
                .ifPresent(this::raisePreviousSubmissionValidationEx);
    }

    private void raisePreviousSubmissionValidationEx(TimeRecord record) {
        throw new TimeRecordErrorException(
                TimeRecordErrorCode.PREVIOUS_UNSUBMITTED_RECORD,
                new SimpleTimeRecordView(record)
        );
    }
}
