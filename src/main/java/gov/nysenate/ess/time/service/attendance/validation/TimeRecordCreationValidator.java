package gov.nysenate.ess.time.service.attendance.validation;

import gov.nysenate.ess.core.model.period.PayPeriod;

/**
 * Determines whether or not a new time record can be explicitly created for a user
 */
public interface TimeRecordCreationValidator {

    /**
     * Checks that a time record can be created for the given employee for the given pay period
     *
     * @param empId int - employee id
     * @param period {@link PayPeriod} - pay period
     * @throws TimeRecordCreationNotPermittedEx - if a record cannot be created for the requested employee/period
     */
    void validateRecordCreation(int empId, PayPeriod period) throws TimeRecordCreationNotPermittedEx;
}
