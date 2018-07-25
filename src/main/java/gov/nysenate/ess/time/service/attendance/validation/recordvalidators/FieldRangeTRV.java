package gov.nysenate.ess.time.service.attendance.validation.recordvalidators;

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
 * Checks time records to make sure that time entry values are within an acceptable range
 */
@Service
public class FieldRangeTRV implements TimeRecordValidator {

    private static final Logger logger = LoggerFactory.getLogger(FieldRangeTRV.class);

    /** Defines max hours worked in a day */
    private static final BigDecimal workMax = new BigDecimal(24);
    /** Defines max non-work hours used in a day for annual employees */
    private static final BigDecimal annualEmpNonWorkMax = new BigDecimal(12);

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) {
        // If the record was saved by an employee
        return record.getScope() == TimeRecordScope.EMPLOYEE;
    }

    /**
     * Check hour entry values for each entry in the time record
     *
     * @param record TimeRecord - A posted time record in the process of validation
     * @param previousState TimeRecord - The most recently saved version of the posted time record
     * @throws TimeRecordErrorException if the submitted record contains invalid hour values
     */
    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) throws TimeRecordErrorException {
        record.getTimeEntries().forEach(this::checkAllFieldsMaxMin);
    }

    /* --- Internal Methods --- */

    /**
     * Check all hour values for the given time entry
     */
    private void checkAllFieldsMaxMin(TimeEntry entry)  throws TimeRecordErrorException {

        // Set the non-work hour maximum depending on the employee's pay type
        BigDecimal nonWorkMax = entry.getPayType() == PayType.TE ? BigDecimal.ZERO : annualEmpNonWorkMax;

        checkFieldMaxMin("workHours", entry.getWorkHours(), workMax);
        checkFieldMaxMin("travelHours", entry.getTravelHours(), nonWorkMax);
        checkFieldMaxMin("holidayHours", entry.getHolidayHours(), nonWorkMax);
        checkFieldMaxMin("vacationHours", entry.getVacationHours(), nonWorkMax);
        checkFieldMaxMin("sickEmpHours", entry.getSickEmpHours(), nonWorkMax);
        checkFieldMaxMin("sickFamHours", entry.getSickFamHours(), nonWorkMax);
        checkFieldMaxMin("miscHours", entry.getMiscHours(), nonWorkMax);
    }

    private void checkFieldMaxMin(String fieldName, Optional<BigDecimal> fieldValueOpt, BigDecimal maxValue)  throws TimeRecordErrorException {
        checkFieldMaxMin(fieldName, fieldValueOpt, maxValue, BigDecimal.ZERO);
    }


    private void checkFieldMaxMin(String fieldName, Optional<BigDecimal> fieldValueOpt, BigDecimal maxValue, BigDecimal minValue)
            throws TimeRecordErrorException {
        BigDecimal fieldValue = fieldValueOpt.orElse(BigDecimal.ZERO);
        if (fieldValue.compareTo(minValue) < 0) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.FIELD_LESS_THAN_ZERO,
                    new InvalidParameterView("fieldHrs", "decimal",
                            fieldName + " >= " + minValue, fieldValue));

        } else if (fieldValue.compareTo(maxValue) > 0) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.FIELD_GREATER_THAN_MAX,
                    new InvalidParameterView("fieldHrs", "decimal",
                            fieldName + " <= " + maxValue , fieldValue));

        }
    }

}
