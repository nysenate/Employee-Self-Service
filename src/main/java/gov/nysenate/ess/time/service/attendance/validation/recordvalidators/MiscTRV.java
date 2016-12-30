package gov.nysenate.ess.time.service.attendance.validation.recordvalidators;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.time.model.attendance.TimeEntry;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
import gov.nysenate.ess.time.model.attendance.TimeRecordScope;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorCode;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorException;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;


/**
 * Checks time records to make sure that no time record contains partially entered miscellaneous values
 */
@Service
public class MiscTRV implements TimeRecordValidator {

    private static final Logger logger = LoggerFactory.getLogger(MiscTRV.class);

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) {
        // If the saved record contains entries where the employee was not a temporary employee
        return record.getScope() == TimeRecordScope.EMPLOYEE
                &&
                record.getTimeEntries().stream()
                        .anyMatch(entry -> entry.getPayType() != PayType.TE);
    }

    /**
     *  checkTimeRecord check hourly increments for all of the daily records
     *
     * @param record TimeRecord - A posted time record in the process of validation
     * @param previousState TimeRecord - The most recently saved version of the posted time record
     * @throws TimeRecordErrorException if the record contains any errors relating to misc hours
     */
    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) throws TimeRecordErrorException {
        ImmutableList<TimeEntry> entries =  record.getTimeEntries();

        entries.forEach(this::checkMisc);
    }

    /**
     * checkMisc:  check misc field values
     *
     * @param entry {@link TimeEntry}
     * @throws TimeRecordErrorException if misc type doesn't accompany misc hours and vice versa
     */
    private void checkMisc(TimeEntry entry)  throws TimeRecordErrorException {

        // True if misc hours are not null and greater than 0
        boolean miscHoursPresent = entry.getMiscHours().orElse(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) > 0;
        // True if there is a misc type on the entry
        boolean miscTypePresent = entry.getMiscType() != null;

        if (miscHoursPresent && !miscTypePresent) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.MISSING_MISC_TYPE,
                    new InvalidParameterView("miscType", "decimal",
                            "A misc type must be specified for misc hours",
                            entry.getMiscHours().orElse(null)));

        }

        if (!miscHoursPresent && miscTypePresent) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.MISSING_MISC_HOURS,
                    new InvalidParameterView("miscType", "decimal",
                            "Misc hours must be present when a misc type is specified",
                            entry.getMiscType()));
        }
    }

}
