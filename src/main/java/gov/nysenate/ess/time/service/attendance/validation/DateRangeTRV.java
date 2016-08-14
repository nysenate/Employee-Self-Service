package gov.nysenate.ess.time.service.attendance.validation;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.time.model.attendance.TimeEntry;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
import gov.nysenate.ess.time.model.attendance.TimeRecordScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;


/**
 * Checks time records to make sure that no time record contains partially entered miscellaneous values
 */

@Service
public class DateRangeTRV implements TimeRecordValidator {

    private static final Logger logger = LoggerFactory.getLogger(DateRangeTRV.class);

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState,TimeRecordAction action) {
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
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) throws TimeRecordErrorException {
        ImmutableList<TimeEntry> entries =  record.getTimeEntries();
        LocalDate entryDate;

        for (TimeEntry entry : entries) {

            entryDate = entry.getDate();

            if (entryDate.isBefore(record.getBeginDate())||entryDate.isAfter(record.getEndDate())) {
                throw new TimeRecordErrorException(TimeRecordErrorCode.DATE_OUT_OF_RANGE,
                        new InvalidParameterView("dateRange", "string",
                                 " entryDate = " + entryDate.toString(), entryDate.toString()));

            }

        }

    }


}
