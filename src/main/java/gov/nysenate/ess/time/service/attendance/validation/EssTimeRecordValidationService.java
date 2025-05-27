package gov.nysenate.ess.time.service.attendance.validation;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EssTimeRecordValidationService implements TimeRecordValidationService {
    private final ImmutableList<TimeRecordValidator> timeRecordValidators;

    @Autowired
    public EssTimeRecordValidationService(List<TimeRecordValidator> timeRecordValidators) {
        this.timeRecordValidators = ImmutableList.copyOf(timeRecordValidators);
    }

    @Override
    public void validateTimeRecord(TimeRecord currRecord, TimeRecord newRecord, TimeRecordAction action) throws InvalidTimeRecordException {
        Optional<TimeRecord> currRecordOpt = Optional.ofNullable(currRecord);
        Map<TimeRecordErrorCode, ViewObject> errors = new EnumMap<>(TimeRecordErrorCode.class);
        timeRecordValidators.stream()
                .filter(validator -> validator.isApplicable(newRecord, currRecordOpt, action))
                .forEach(validator -> {
                    try {
                        validator.checkTimeRecord(newRecord, currRecordOpt, action);
                    } catch (TimeRecordErrorException ex) {
                        errors.put(ex.getCode(), ex.getErrorData());
                    }
                });
        if (!errors.isEmpty()) {
            throw new InvalidTimeRecordException(newRecord, errors);
        }
    }
}
