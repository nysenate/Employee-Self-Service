package gov.nysenate.ess.time.service.attendance.validation.recordvalidators;

import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.time.model.attendance.*;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorCode;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorException;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * Checks action of submitted time record to ensure that it is valid for the time record's current status
 */
@Service
public class ScopeActionTRV implements TimeRecordValidator {

    private static final Logger logger = LoggerFactory.getLogger(ScopeActionTRV.class);

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) {
        // This Time Record Validator is Applicable in all scenarios.
        return true;
    }

    /**
     * Checks action of submitted time record to ensure that it is valid for the time record's current status
     *
     * @param record TimeRecord - A posted time record in the process of validation
     * @param prevState Optional<TimeRecord> - The most recently saved version of the posted time record
     * @throws TimeRecordErrorException If the submitted action is invalid
     */
    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> prevState, TimeRecordAction action) throws TimeRecordErrorException {
           if (prevState.isPresent()) {
               TimeRecord previousState = prevState.get();
               TimeRecordStatus timeRecordStatus = previousState.getRecordStatus();
               try {
                   timeRecordStatus.getResultingStatus(action);
               }
               catch (InvalidTimeRecordActionEx ex) {
                   throw new TimeRecordErrorException(TimeRecordErrorCode.INVALID_SCOPE_ACTION,
                           new InvalidParameterView("action", "TimeRecordAction",
                                  "action should be valid for current time record status: " +
                                   timeRecordStatus + ". Valid actions include: " + timeRecordStatus.getValidActions(),
                                   action));
               }
           }
    }

}
