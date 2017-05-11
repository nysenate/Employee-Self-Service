package gov.nysenate.ess.time.service.attendance.validation.recordvalidators;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.ShiroUtils;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
import gov.nysenate.ess.time.model.attendance.TimeRecordScope;
import gov.nysenate.ess.time.model.personnel.ExtendedSupEmpGroup;
import gov.nysenate.ess.time.model.personnel.SupervisorException;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorCode;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorException;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordValidator;
import gov.nysenate.ess.time.service.personnel.SupervisorInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This validator ensures that users do not save time records that are out of the appropriate scope
 * E.g. A supervisor cannot save a record in employee scope and vice versa.
 */
@Service
public class PermittedUserScopeTRV implements TimeRecordValidator
{

    private static final Logger logger = LoggerFactory.getLogger(PermittedUserScopeTRV.class);

    @Autowired private SupervisorInfoService supInfoService;

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) {
        // Everyone and every condition is applicable for this check
        return true;
    }

    /**
     * Ensures that the authenticated user is permitted to modify this time record
     *
     * @param record {@link TimeRecord} - A posted time record in the process of validation
     * @param previousState {@link Optional<TimeRecord>} - The most recently saved version of the posted time record
     * @param action {@link TimeRecordAction} - The requested action to be performed on the time record
     * @throws TimeRecordErrorException if the user is not in the appropriate scope
     */
    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action)
            throws TimeRecordErrorException {
        // Only check if previous record exists
        if (!previousState.isPresent()) {
            return;
        }
        TimeRecord prevRecord = previousState.get();
        TimeRecordScope prevScope = prevRecord.getScope();
        int empId = ShiroUtils.getAuthenticatedEmpId();

        switch (prevScope) {
            case EMPLOYEE:
                testEmployeeScope(prevRecord, empId);
                break;
            case SUPERVISOR:
                testSupervisorScope(prevRecord, empId);
                break;
            case PERSONNEL:
//                testPersonnelScope(prevRecord, empId);
                break;
            default:
                throw new IllegalStateException("Time record (id: " + record.getTimeRecordId() +
                        ") has invalid scope: " + prevScope);
        }
    }

    /* --- Internal Methods --- */

    /**
     * Test the given record to ensure that it belongs to the employee with the given emp id
     * @param prevRecord {@link TimeRecord}
     * @param empId int
     * @throws TimeRecordErrorException if the employee is not the owner of the given record
     */
    private void testEmployeeScope(TimeRecord prevRecord, int empId) throws TimeRecordErrorException {
        if (prevRecord.getEmployeeId().equals(empId)) {
            return;
        }
        throw new TimeRecordErrorException(TimeRecordErrorCode.INVALID_TIME_RECORD_SCOPE);
    }

    /**
     * Test the given record to ensure that it is supervised the employee with the given emp id
     * @param prevRecord {@link TimeRecord}
     * @param empId int
     * @throws TimeRecordErrorException if the employee is not the supervisor of the given record
     */
    private void testSupervisorScope(TimeRecord prevRecord, int empId) {
        // Check if the employee is listed as a supervisor on the time record
        if (prevRecord.getSupervisorId().equals(empId)) {
            return;
        }

        try {
            // Check if the employee is responsible for the employee during the record period (in case of an override)
            ExtendedSupEmpGroup supervisorEmpGroup = supInfoService.getExtendedSupEmpGroup(empId, Range.all());
            if (supervisorEmpGroup.hasExtEmployeeDuringRange(prevRecord.getEmployeeId(), prevRecord.getDateRange())) {
                return;
            }
        } catch (SupervisorException ignored) {}

        throw new TimeRecordErrorException(TimeRecordErrorCode.INVALID_TIME_RECORD_SCOPE);
    }

    /**
     * Throw an exception as personnel scope is not currently supported
     * @param prevRecord {@link TimeRecord}
     * @param empId int
     * @throws TimeRecordErrorException if the employee is not the owner of the given record
     */
    private void testPersonnelScope(TimeRecord prevRecord, int empId) {
        throw new IllegalStateException("Saving of personnel scope records is currently not supported");
    }

}
