package gov.nysenate.ess.seta.service.attendance.validation;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.seta.dao.attendance.TimeRecordDao;
import gov.nysenate.ess.seta.model.attendance.TimeRecord;
import gov.nysenate.ess.seta.service.attendance.TimeRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class EssTimeRecordValidationService implements TimeRecordValidationService {

    private static final Logger logger = LoggerFactory.getLogger(EssTimeRecordValidationService.class);

    @Autowired private TimeRecordService timeRecordService;
    @Autowired private TimeRecordDao timeRecordDao;

    @Autowired private PermittedModificationTRV permittedModificationTRV;
    @Autowired private AllowanceTRV allowanceTRV;
    @Autowired private AccrualTRV accrualTRV;

    private ImmutableList<TimeRecordValidator> timeRecordValidators;

    @PostConstruct
    public void init() {
        timeRecordValidators = ImmutableList.<TimeRecordValidator>builder()
                .add(permittedModificationTRV)
                .add(allowanceTRV)
                .add(accrualTRV)
                // TODO: ADD SOME more VALIDATORS
                .build();
    }

    @Override
    public void validateTimeRecord(TimeRecord record) throws InvalidTimeRecordException {
        Optional<TimeRecord> prevState = getPreviousState(record);
        Map<TimeRecordErrorCode, ViewObject> errors = new EnumMap<>(TimeRecordErrorCode.class);
        timeRecordValidators.stream()
                .filter(validator -> validator.isApplicable(record, prevState))
                .forEach(validator -> {
                    try {
                        validator.checkTimeRecord(record, prevState);
                    } catch (TimeRecordErrorException ex) {
                        errors.put(ex.getCode(), ex.getErrorData());
                    }
                });
        if (!errors.isEmpty()) {
            throw new InvalidTimeRecordException(record, errors);
        }
    }

    /** --- Internal Methods --- */

    /**
     * Get the previous state of a time record or an empty optional if no record existed
     * @param record TimeRecord
     * @return Optional<TimeRecord>
     */
    Optional<TimeRecord> getPreviousState(TimeRecord record) {
        // Return empty if the record doesn't have an id
        if (record.getTimeRecordId() == null) {
            return Optional.empty();
        }
        // Try getting the record from the cached active records first
        Optional<TimeRecord> prevState = timeRecordService.getActiveTimeRecords(record.getEmployeeId()).stream()
                .filter(oRec -> record.getTimeRecordId().equals(oRec.getTimeRecordId()))
                .findAny();
        // If that fails, try to get it from the dao
        if (!prevState.isPresent()) {
            try {
                prevState = Optional.of(timeRecordDao.getTimeRecord(record.getTimeRecordId()));
            } catch (EmptyResultDataAccessException ignored) {}
        }
        return prevState;
    }
}
