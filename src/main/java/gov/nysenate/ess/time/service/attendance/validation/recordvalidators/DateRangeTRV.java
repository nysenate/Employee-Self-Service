package gov.nysenate.ess.time.service.attendance.validation.recordvalidators;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.time.model.attendance.TimeEntry;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorCode;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorException;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;


/**
 * Checks time records to ensure that they contain no entries that fall outside of their specified date range
 */
@Service
public class DateRangeTRV implements TimeRecordValidator {

    private static final Logger logger = LoggerFactory.getLogger(DateRangeTRV.class);

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState,TimeRecordAction action) {
        return true;
    }

    /**
     * Check time record for entries that fall outside of the record's date range.
     *
     * @param record TimeRecord - A posted time record in the process of validation
     * @param previousState TimeRecord - The most recently saved version of the posted time record
     * @throws TimeRecordErrorException - if time entries exist outside of the record's date range
     */
    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) throws TimeRecordErrorException {
        ImmutableList<TimeEntry> entries =  record.getTimeEntries();
        LocalDate entryDate;

        for (TimeEntry entry : entries) {

            entryDate = entry.getDate();

            if (entryDate == null)  {
                throw new TimeRecordErrorException(TimeRecordErrorCode.NULL_DATE,
                        new InvalidParameterView("date", "string",
                                "time entry date cannot be null", null));
            }

            if (!record.getDateRange().contains(entryDate)) {
                throw new TimeRecordErrorException(TimeRecordErrorCode.DATE_OUT_OF_RANGE,
                        new InvalidParameterView("date", "string",
                                "time entry date must fall within time record date range",
                                entryDate));
            }
        }
    }
}
