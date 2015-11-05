package gov.nysenate.ess.seta.service.attendance.validation;

import gov.nysenate.ess.seta.model.attendance.TimeRecord;

import java.util.Optional;

/**
 * An interface for a class that performs time record validation according to a specific time record rule
 */
public interface TimeRecordValidator {

    /**
     * Tests to see if this validation rule applies to the given time record and previous record state
     * @param record TimeRecord - A posted time record in the process of validation
     * @param previousState TimeRecord - The most recently saved version of the posted time record
     * @return boolean - true iff the rule can be applied
     */
    boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState);

    /**
     * Performs a check on a time record, throwing an exception if the time record is found to be invalid
     * @param record TimeRecord - A posted time record in the process of validation
     * @param previousState TimeRecord - The most recently saved version of the posted time record
     * @throws TimeRecordErrorException if the provided time record contains erroneous data
     */
    void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState) throws TimeRecordErrorException;

}
