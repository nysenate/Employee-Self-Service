package gov.nysenate.ess.time.service.attendance.validation;

import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;

/**
 * A service that validates time records according to NY Senate rules
 */
public interface TimeRecordValidationService
{
    /**
     * Checks the given TimeRecord and TimeRecordAction for inconsistencies with NY Senate time and attendence policy
     * Ensures that :
     *  - All used accrual time is subtractable from the employees accruals to date
     *  - The employee is permitted to use accrual time if it is spent
     *  - The employee won't exceed their annual permitted accrual usage
     *  - Temporary employees do not exceed their pay allowance
     *  - A time record for this pay period has not already been submitted
     *  - The pay period for this time record is valid
     *
     * @param currRecord {@link TimeRecord} The current record for this period
     * @param newRecord {@link TimeRecord} The time record that will be saved
     * @param action {@link TimeRecordAction} the requested action to be performed on the time record
     * @throws InvalidTimeRecordException if the given time record is not valid
     */
    void validateTimeRecord(TimeRecord currRecord, TimeRecord newRecord, TimeRecordAction action) throws InvalidTimeRecordException;
}
