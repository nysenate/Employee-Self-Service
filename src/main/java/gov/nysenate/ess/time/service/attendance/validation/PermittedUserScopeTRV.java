package gov.nysenate.ess.time.service.attendance.validation;

import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.time.model.attendance.TimeRecordScope;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This validator ensures that new time records are not saved by users
 * and that saved time records do not contain illegal modifications
 */
@Service
public class PermittedUserScopeTRV implements TimeRecordValidator
{
    private static final Logger logger = LoggerFactory.getLogger(PermittedUserScopeTRV.class);
    private TimeRecord record;


    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) {
        // isApplicable must be called before checking the record, so setting the TimeRecord to be used during the
        // record check.
        this.record = record;

        // Everyone and every condition is applicable for this check
        return true;
    }

    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action)
            throws TimeRecordErrorException {
        TimeRecordScope timeRecordScope = record.getScope();

        if (timeRecordScope.equals(TimeRecordScope.SUPERVISOR)) {
            logger.info("***Needs supervisor permissions");
            checkUserScope(record, "supervisor");


        } else if (timeRecordScope.equals(TimeRecordScope.PERSONNEL)) {
            logger.info("***Needs personnel permissions");
            checkUserScope(record, "personnel");

        } else if (!timeRecordScope.equals(TimeRecordScope.EMPLOYEE)){
            logger.info("***Invalid Scope");

            if (timeRecordScope == null) {
                new InvalidParameterView("InvalidScope", "string",
                        " Scope Code =  null ", "null");
            }
            else {
                new InvalidParameterView("InvalidScope", "string",
                        " Scope Code = " + timeRecordScope.getCode(), timeRecordScope.getCode());
            }

        }

    }

    public void checkUserScope(TimeRecord record, String neededRole)
            throws TimeRecordErrorException {

        Subject subject = SecurityUtils.getSubject();

        if (!subject.hasRole(neededRole)) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.ENTRY_CANNOT_CHANGE_IN_SCOPE,
                    new InvalidParameterView(neededRole, "string",
                            " Needs " + " = " +neededRole + " permission", neededRole));

        }

        /**
         *
         * Supervisor should not be able to set his/her own Timesheet Record
         * in any way when the Timesheet is in his/her supervisor's hands (
         *
         */

        else if (neededRole.equalsIgnoreCase("supervisor")) {
            SenatePerson person = (SenatePerson) subject.getPrincipal();
            int empId = person.getEmployeeId();
            if (empId == record.getEmployeeId().intValue()) {

                throw new TimeRecordErrorException(TimeRecordErrorCode.SUPERVISORS_OWN_TIMESHEET,
                        new InvalidParameterView(neededRole, "string",
                                " Pay Period  = "+ record.getDateRange().toString() , neededRole));

            }

        }
    }

    /** --- Internal Methods --- */


}
