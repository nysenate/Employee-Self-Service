package gov.nysenate.ess.time.service.attendance.validation;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.dao.attendance.TimeRecordDao;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
import gov.nysenate.ess.time.service.attendance.TimeRecordService;
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
    @Autowired private HourIncrementTRV hourIncrementTRV;
    @Autowired private MiscTRV miscTRV;
    @Autowired private TotalTRV totalTRV;
    @Autowired private FieldRangeTRV fieldMaxMinTRV;
    @Autowired private DateRangeTRV dateRangeTRV;
    @Autowired private ChangeTRV changeTRV;
    @Autowired private PermittedUserScopeTRV permittedUserScopeTRV;
    @Autowired private ScopePermissionTRV scopePermissionTRV;
    @Autowired private ScopeActionTRV recordScopeTRV;

    private ImmutableList<TimeRecordValidator> timeRecordValidators;

    @PostConstruct
    public void init() {
        timeRecordValidators = ImmutableList.<TimeRecordValidator>builder()
                .add(changeTRV)
                .add(permittedModificationTRV)
                .add(allowanceTRV)
                .add(accrualTRV)
                .add(hourIncrementTRV)
                .add(miscTRV)
                .add(totalTRV)
                .add(fieldMaxMinTRV)
                .add(dateRangeTRV)
                .add(recordScopeTRV)
                .add(scopePermissionTRV)
                .add(permittedUserScopeTRV)
                // TODO: ADD SOME more VALIDATORS
                .build();
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

    /** --- Internal Methods --- */

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
