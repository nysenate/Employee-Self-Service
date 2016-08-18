package gov.nysenate.ess.time.service.attendance.validation;

import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.time.model.attendance.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * Checks time records to make sure that no time record contains partially entered miscellaneous values
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
     *  checkTimeRecord check hourly increments for all of the daily records
     *
     * @param record TimeRecord - A posted time record in the process of validation
     * @param prevState Optional<TimeRecord> - The most recently saved version of the posted time record
     * @throws TimeRecordErrorException
     */

    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> prevState, TimeRecordAction action) throws TimeRecordErrorException {
           if (prevState.isPresent()) {
               TimeRecord previousState = prevState.get();
               TimeRecordStatus timeRecordStatus = previousState.getRecordStatus();
               TimeRecordStatus nextTimeRecordStatus = null;
               try {
                   nextTimeRecordStatus = timeRecordStatus.getResultingStatus(action);
               }
               catch (InvalidTimeRecordActionEx ex) {

                   throw new TimeRecordErrorException(TimeRecordErrorCode.INVALID_STATUS_CHANGE,
                           new InvalidParameterView(timeRecordStatus.getName(), "string",
                                  " Record Status = " + timeRecordStatus.name() + ", Invalid Action = " + action.name() , action.name()));
               }
           }
    }

}
