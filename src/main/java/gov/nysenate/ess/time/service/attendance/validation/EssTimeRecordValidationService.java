package gov.nysenate.ess.time.service.attendance.validation;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
import gov.nysenate.ess.time.service.attendance.TimeRecordNotFoundException;
import gov.nysenate.ess.time.service.attendance.TimeRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EssTimeRecordValidationService implements TimeRecordValidationService {

    private static final Logger logger = LoggerFactory.getLogger(EssTimeRecordValidationService.class);

    private final TimeRecordService timeRecordService;
    private final ImmutableList<TimeRecordValidator> timeRecordValidators;

    public EssTimeRecordValidationService(TimeRecordService timeRecordService,
                                          List<TimeRecordValidator> timeRecordValidators) {
        this.timeRecordService = timeRecordService;
        this.timeRecordValidators = ImmutableList.copyOf(timeRecordValidators);
    }

    @Override
    public void validateTimeRecord(TimeRecord record, TimeRecordAction action) throws InvalidTimeRecordException {
        Optional<TimeRecord> prevState = getPreviousState(record);
        Map<TimeRecordErrorCode, ViewObject> errors = new EnumMap<>(TimeRecordErrorCode.class);
        timeRecordValidators.stream()
                .filter(validator -> validator.isApplicable(record, prevState, action))
                .forEach(validator -> {
                    try {
                        validator.checkTimeRecord(record, prevState, action);
                    } catch (TimeRecordErrorException ex) {
                        errors.put(ex.getCode(), ex.getErrorData());
                    }
                });
        if (!errors.isEmpty()) {
            throw new InvalidTimeRecordException(record, errors);
        }
    }

    /* --- Internal Methods --- */

    /**
     * Get the previous state of a time record or an empty optional if no record existed
     * @param record TimeRecord
     * @return Optional<TimeRecord>
     */
    private Optional<TimeRecord> getPreviousState(TimeRecord record) {
        // Return empty if the record doesn't have an id
        if (record.getTimeRecordId() == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(timeRecordService.getTimeRecord(record.getTimeRecordId()));
        } catch (TimeRecordNotFoundException ex) {
            return Optional.empty();
        }
//        // Try getting the record from the cached active records first
//        Optional<TimeRecord> prevState = timeRecordService.getActiveTimeRecords(record.getEmployeeId()).stream()
//                .filter(oRec -> record.getTimeRecordId().equals(oRec.getTimeRecordId()))
//                .findAny();
//        // If that fails, try to get it from the dao
//        if (!prevState.isPresent()) {
//            try {
//                prevState = Optional.of(timeRecordService.getTimeRecord(record.getTimeRecordId()));
//            } catch (EmptyResultDataAccessException ignored) {}
//        }
//        return prevState;
    }
}
