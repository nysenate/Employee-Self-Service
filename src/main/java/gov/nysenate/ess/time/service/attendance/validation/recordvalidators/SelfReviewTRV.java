package gov.nysenate.ess.time.service.attendance.validation.recordvalidators;

import gov.nysenate.ess.core.util.ShiroUtils;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorException;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordValidator;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static gov.nysenate.ess.time.model.attendance.TimeRecordScope.EMPLOYEE;
import static gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorCode.REVIEW_OF_OWN_TIMESHEET;

/**
 * Checks to make sure that the authenticated user is not performing a review of their own time record.
 */
@Service
public class SelfReviewTRV implements TimeRecordValidator {
    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) {
        // Employees are allowed to modify and take action on their own records only in employee scope.
        return record.getScope() != EMPLOYEE;
    }

    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) throws TimeRecordErrorException {
        int userEmpId = ShiroUtils.getAuthenticatedEmpId();
        if (userEmpId == record.getEmployeeId()) {
            throw new TimeRecordErrorException(REVIEW_OF_OWN_TIMESHEET);
        }
    }
}
