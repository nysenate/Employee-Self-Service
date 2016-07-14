package gov.nysenate.ess.seta.service.attendance.validation;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.seta.model.attendance.TimeEntry;
import gov.nysenate.ess.seta.model.attendance.TimeRecord;
import gov.nysenate.ess.seta.model.attendance.TimeRecordAction;
import gov.nysenate.ess.seta.model.attendance.TimeRecordScope;
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
        // If the saved record contains entries where the employee was a temporary employee
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
     * @throws TimeRecordErrorException
     */

    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) throws TimeRecordErrorException {
        ImmutableList<TimeEntry> entries =  record.getTimeEntries();

        for (TimeEntry entry : entries) {
            checkMisc(entry);
        }

    }

    /**
     * checkMisc:  check misc field values
     *
     * @param entry
     * @throws TimeRecordErrorException
     */

    private void checkMisc(TimeEntry entry)  throws TimeRecordErrorException {

        if (entry.getMiscHours().isPresent() && entry.getMiscType() == null) {
                throw new TimeRecordErrorException(TimeRecordErrorCode.MISSING_MISC_TYPE,
                        new InvalidParameterView("miscType", "decimal",
                                "miscTime = " +entry.getMiscHours().orElse(new BigDecimal(0)).toString(),  entry.getMiscHours().orElse(new BigDecimal(0)).toString()));

            }
        else if (entry.getMiscType() != null && !entry.getMiscHours().isPresent()) {
                throw new TimeRecordErrorException(TimeRecordErrorCode.MISSING_MISC_HOURS,
                        new InvalidParameterView("miscType", "decimal",
                                "miscTime = " +entry.getMiscHours().orElse(new BigDecimal(0)).toString(),  entry.getMiscHours().orElse(new BigDecimal(0)).toString()));
            }
    }

}
