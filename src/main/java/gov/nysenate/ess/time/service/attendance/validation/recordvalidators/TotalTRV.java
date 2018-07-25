package gov.nysenate.ess.time.service.attendance.validation.recordvalidators;

import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.time.model.attendance.TimeEntry;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
import gov.nysenate.ess.time.model.attendance.TimeRecordScope;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorException;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

import static gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorCode.TOTAL_GREATER_THAN_TWENTYFOUR;
import static gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorCode.TOTAL_LESS_THAN_ZERO;


/**
 * Checks time entries to ensure that all entries add up to a possible hour value.
 */
@Service
public class TotalTRV implements TimeRecordValidator {

    private static final Logger logger = LoggerFactory.getLogger(TotalTRV.class);

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) {
        // If the record was saved by an employee
        return record.getScope() == TimeRecordScope.EMPLOYEE;
    }

    /**
     * Check entry totals for the time record.
     *
     * @param record TimeRecord - A posted time record in the process of validation
     * @param previousState TimeRecord - The most recently saved version of the posted time record
     * @throws TimeRecordErrorException if the total for any time entry is unacceptable.
     */
    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) throws TimeRecordErrorException {
        record.getTimeEntries().forEach(this::checkTotal);
    }

    /**
     * Check the sum of all hours in a time entry
     */
    private void checkTotal(TimeEntry entry)  throws TimeRecordErrorException {
        final BigDecimal twentyFour = new BigDecimal(24);
        BigDecimal totalHours = entry.getTotalHours();

        if (totalHours.compareTo(BigDecimal.ZERO) < 0) {
            throw new TimeRecordErrorException(TOTAL_LESS_THAN_ZERO,
                    new InvalidParameterView("totalHours", "decimal",
                            "total entry hours cannot be negative", totalHours));
        }

        if (totalHours.compareTo(twentyFour) > 0) {
            throw new TimeRecordErrorException(TOTAL_GREATER_THAN_TWENTYFOUR,
                    new InvalidParameterView("totalHours", "decimal",
                            "total hours cannot exceed 24 for a single day", totalHours));
        }
    }
}
