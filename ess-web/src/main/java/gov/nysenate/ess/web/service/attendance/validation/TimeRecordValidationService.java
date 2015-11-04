package gov.nysenate.ess.web.service.attendance.validation;

import gov.nysenate.ess.web.model.attendance.TimeRecord;

/**
 * A service that validates time records according to NY Senate rules
 */
public interface TimeRecordValidationService
{
    /**
     * Checks the given TimeRecord for inconsistencies with NY Senate time and attendence policy
     * Ensures that :
     *  - All used accrual time is subtractable from the employees accruals to date
     *  - The employee is permitted to use accrual time if it is spent
     *  - The employee won't exceed their annual permitted accrual usage
     *  - Temporary employees do not exceed their pay allowance
     *  - A time record for this pay period has not already been submitted
     *  - The pay period for this time record is valid
     *
     * @param record
     * @throws InvalidTimeRecordException if the given time record is not valid
     */
    public void validateTimeRecord(TimeRecord record) throws InvalidTimeRecordException;
}
