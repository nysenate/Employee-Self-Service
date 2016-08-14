package gov.nysenate.ess.time.service.attendance.validation;

import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;

import java.util.Optional;

/**
 * An interface for a class that performs time record validation according to a specific time record rule
 */
public interface TimeRecordValidator {

    /**
     * Tests to see if this validation rule applies to the given time record and previous record state
     * @param record {@link TimeRecord} - A posted time record in the process of validation
     * @param previousState {@link Optional<TimeRecord>} - The most recently saved version of the posted time record
     * @param action {@link TimeRecordAction} - The requested action to be performed on the time record
     * @return {@link Boolean} - true iff the rule can be applied
     */
    boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action);

    /**
     * Performs a check on a time record, throwing an exception if the time record is found to be invalid
     * @param record {@link TimeRecord} - A posted time record in the process of validation
     * @param previousState {@link Optional<TimeRecord>} - The most recently saved version of the posted time record
     * @param action {@link TimeRecordAction} - The requested action to be performed on the time record
     * @throws TimeRecordErrorException if the provided time record contains erroneous data
     */
    void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action)
            throws TimeRecordErrorException;

}
