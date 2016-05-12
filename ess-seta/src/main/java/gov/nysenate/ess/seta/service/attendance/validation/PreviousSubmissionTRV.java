package gov.nysenate.ess.seta.service.attendance.validation;

import gov.nysenate.ess.seta.client.view.SimpleTimeRecordView;
import gov.nysenate.ess.seta.model.attendance.TimeRecord;
import gov.nysenate.ess.seta.model.attendance.TimeRecordScope;
import gov.nysenate.ess.seta.service.attendance.TimeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * A validation that is performed when an employee submits a time record to supervisor.
 * Checks to ensure that there are no unsubmitted annual pay time records with earlier dates
 */
@Service
public class PreviousSubmissionTRV implements TimeRecordValidator {

    @Autowired
    TimeRecordService timeRecordService;

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState) {
        TimeRecordScope previousScope = previousState
                .map(TimeRecord::getScope)
                .orElse(TimeRecordScope.EMPLOYEE);
        TimeRecordScope currentScope = record.getScope();
        return TimeRecordScope.EMPLOYEE == previousScope && currentScope != previousScope;
    }

    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState) throws TimeRecordErrorException {
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
