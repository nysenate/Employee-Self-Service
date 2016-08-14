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
import java.util.Optional;


/**
 * Checks time records to make sure that no time record contains partially entered miscellaneous values
 */

@Service
public class FieldRangeTRV implements TimeRecordValidator {

    private static final Logger logger = LoggerFactory.getLogger(FieldRangeTRV.class);

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) {
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
        int x = -1;

        for (TimeEntry entry : entries) {
            checkAllFieldsMaxMin(entry);
        }

    }

    /**
     * checkTotal:  check row totals
     *
     * @param entry
     * @throws TimeRecordErrorException
     */

    private void checkAllFieldsMaxMin(TimeEntry entry)  throws TimeRecordErrorException {

        BigDecimal workMax = new BigDecimal(21);
        BigDecimal nonWorkMax = new BigDecimal(12);

        if (entry.getPayType().toString().equals("TE")) {
            nonWorkMax = BigDecimal.ZERO;
        }

        checkFieldMaxMin("workHours", entry.getWorkHours().orElse(BigDecimal.ZERO), workMax);
        checkFieldMaxMin("travelHours", entry.getTravelHours().orElse(BigDecimal.ZERO), nonWorkMax);
        checkFieldMaxMin("holidayHours", entry.getHolidayHours().orElse(BigDecimal.ZERO), nonWorkMax);
        checkFieldMaxMin("vacationHours", entry.getVacationHours().orElse(BigDecimal.ZERO), nonWorkMax);
        checkFieldMaxMin("sickEmpHours", entry.getSickEmpHours().orElse(BigDecimal.ZERO), nonWorkMax);
        checkFieldMaxMin("sickFamHours", entry.getSickFamHours().orElse(BigDecimal.ZERO), nonWorkMax);
        checkFieldMaxMin("miscHours", entry.getMiscHours().orElse(BigDecimal.ZERO), nonWorkMax);

    }

    private void checkFieldMaxMin(String fieldName, BigDecimal fieldValue, BigDecimal maxValue)  throws TimeRecordErrorException {
        checkFieldMaxMin(fieldName, fieldValue, maxValue, BigDecimal.ZERO);
    }


   private void checkFieldMaxMin(String fieldName, BigDecimal fieldValue, BigDecimal maxValue, BigDecimal minValue)  throws TimeRecordErrorException {
       if (fieldValue.compareTo(minValue) < 0) {
           logger.info("    Less than "+minValue.toString()+".");
           throw new TimeRecordErrorException(TimeRecordErrorCode.FIELD_LESS_THAN_ZERO,
                   new InvalidParameterView("fieldHrs", "decimal",
                           fieldName + " = " + fieldValue.toString(), fieldValue.toString()));

       } else if (fieldValue.compareTo(maxValue) > 0) {

           throw new TimeRecordErrorException(TimeRecordErrorCode.FIELD_GREATER_THAN_MAX,
                   new InvalidParameterView("fieldHrs", "decimal",
                           fieldName + " = " + fieldValue.toString(), fieldValue.toString()));

       }

   }

}
