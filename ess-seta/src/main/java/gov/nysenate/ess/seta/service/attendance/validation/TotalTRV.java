package gov.nysenate.ess.seta.service.attendance.validation;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.seta.model.attendance.TimeEntry;
import gov.nysenate.ess.seta.model.attendance.TimeRecord;
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
public class TotalTRV implements TimeRecordValidator {

    private static final Logger logger = LoggerFactory.getLogger(TotalTRV.class);

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState) {
        // If the saved record contains entries where the employee was a temporary employee
        return record.getScope() == TimeRecordScope.EMPLOYEE;
    }

    /**
     *  checkTimeRecord check hourly increments for all of the daily records
     *
     * @param record TimeRecord - A posted time record in the process of validation
     * @param previousState TimeRecord - The most recently saved version of the posted time record
     * @throws TimeRecordErrorException
     */

    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState) throws TimeRecordErrorException {
        ImmutableList<TimeEntry> entries =  record.getTimeEntries();

        for (TimeEntry entry : entries) {
            checkTotal(entry);
        }

    }

    /**
     * checkTotal:  check row totals
     *
     * @param entry
     * @throws TimeRecordErrorException
     */

    private void checkTotal(TimeEntry entry)  throws TimeRecordErrorException {

        BigDecimal totalHours = entry.getTotalHours();
        BigDecimal twentyFour = new BigDecimal(24);
        if (totalHours.compareTo(BigDecimal.ZERO) < 0) {
            logger.info("    Less than zero.");
            throw new TimeRecordErrorException(TimeRecordErrorCode.TOTAL_LESS_THAN_ZERO,
                    new InvalidParameterView("totalHrs", "decimal",
                            "totalHrs = " + totalHours.toString(), totalHours.toString()));

        } else if (totalHours.compareTo(twentyFour) > 0) {

            logger.info("    Greater than twentryFour.");
            throw new TimeRecordErrorException(TimeRecordErrorCode.TOTAL_GREATER_THAN_TWENTYFOUR,
                    new InvalidParameterView("totalHrs", "decimal",
                            "totalHrs = " + totalHours.toString(), totalHours.toString()));

        }
    }

}
