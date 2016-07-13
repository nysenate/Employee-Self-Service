package gov.nysenate.ess.seta.service.attendance.validation;

import gov.nysenate.ess.seta.client.view.SimpleTimeRecordView;
import gov.nysenate.ess.seta.model.attendance.TimeRecord;
import gov.nysenate.ess.seta.model.attendance.TimeRecordAction;
import gov.nysenate.ess.seta.model.attendance.TimeRecordScope;
import gov.nysenate.ess.seta.service.attendance.TimeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A validation that is performed when an employee submits a time record to supervisor.
 * Checks to ensure that there are no unsubmitted annual pay time records with earlier dates
 */
@Service
public class PreviousSubmissionTRV implements TimeRecordValidator {

    @Autowired
    TimeRecordService timeRecordService;

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
