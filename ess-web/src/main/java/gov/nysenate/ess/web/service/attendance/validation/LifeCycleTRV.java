package gov.nysenate.ess.web.service.attendance.validation;

import com.google.common.collect.Sets;
import gov.nysenate.ess.web.client.view.error.InvalidParameterView;
import gov.nysenate.ess.web.model.attendance.TimeRecord;
import gov.nysenate.ess.web.model.attendance.TimeRecordStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

/**
 * Checks to ensure that a posted time record is following a valid progression through the time record life cycle
 * as indicated by changes in time record status
 */
@Service
public class LifeCycleTRV implements TimeRecordValidator {

    /**
     * Applicable for all posted time records
     */
    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState) {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState) throws TimeRecordErrorException {
        Optional<TimeRecordStatus> newStatus = Optional.ofNullable(record.getRecordStatus());
        Optional<TimeRecordStatus> prevStatus = previousState.map(TimeRecord::getRecordStatus);
        // Get valid statuses that occur after previous status and ensure that the new status is contained in this set
        Set<TimeRecordStatus> validStatuses = getValidStatuses(prevStatus);
        if (!newStatus.isPresent() || !validStatuses.contains(newStatus.get())) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.INVALID_STATUS_CHANGE,
                    new InvalidParameterView("recordStatus", "TimeRecordStatus",
                            prevStatus.map(Enum::name).orElse("null") + " -> " + validStatuses.toString(),
                            newStatus.map(Enum::name).orElse("null")));
        }
    }

    /** --- Internal Methods --- */

    /**
     * Get a set of statuses that can follow the previous status
     */
    private static Set<TimeRecordStatus> getValidStatuses(Optional<TimeRecordStatus> prevStatus) {
        if (!prevStatus.isPresent()) {
            return Sets.newHashSet(TimeRecordStatus.NOT_SUBMITTED);
        }
        switch (prevStatus.get()) {
            case NOT_SUBMITTED:
            case DISAPPROVED:
            case DISAPPROVED_PERSONNEL:
                return Sets.newHashSet(TimeRecordStatus.NOT_SUBMITTED, TimeRecordStatus.SUBMITTED);
            case SUBMITTED:
                return Sets.newHashSet(TimeRecordStatus.DISAPPROVED, TimeRecordStatus.APPROVED);
            case APPROVED:
            case SUBMITTED_PERSONNEL:
                return Sets.newHashSet(TimeRecordStatus.DISAPPROVED_PERSONNEL, TimeRecordStatus.APPROVED_PERSONNEL);
            case APPROVED_PERSONNEL:
                return Sets.newHashSet(TimeRecordStatus.APPROVED_PERSONNEL);
            default:
                throw new IllegalArgumentException("previous time record status canoot be " + prevStatus + "!");
        }
    }
}
