package gov.nysenate.ess.time.service.attendance.validation.recordvalidators;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.core.model.payroll.PayType;
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

import static gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorCode.INVALID_HOURLY_INCREMENT;
import static java.math.BigDecimal.ZERO;

/**
 * Checks time records to make sure that hour values are within allowed increments.
 */
@Service
public class HourIncrementTRV implements TimeRecordValidator {

    private static final Logger logger = LoggerFactory.getLogger(HourIncrementTRV.class);

    private static final BigDecimal annualHourIncrement = new BigDecimal("0.5");
    private static final BigDecimal teHourIncrement = new BigDecimal("0.25");

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) {
        // If the record was saved by an employee
        return record.getScope() == TimeRecordScope.EMPLOYEE;
    }

    /**
     * Check hourly increments for all of the daily records
     *
     * @param record TimeRecord - A posted time record in the process of validation
     * @param previousState TimeRecord - The most recently saved version of the posted time record
     * @param action {@link TimeRecordAction}
     * @throws TimeRecordErrorException if any hourly increments are off
     */
    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action)
            throws TimeRecordErrorException {
        ImmutableList<TimeEntry> entries =  record.getTimeEntries();

        for (TimeEntry entry : entries) {
            checkHourIncrements(entry);
        }
    }

    /* --- Internal Methods --- */

    /**
     * Check entry increments using a method that depends on the entry's pay type.
     */
    private void checkHourIncrements(TimeEntry entry)  throws TimeRecordErrorException {
        if (entry.getPayType() == PayType.TE) {
            checkTeHourIncrements(entry);
        }
        else {
            checkRaSaHourIncrements(entry);
        }
    }

    /**
     * Check Temporary fields hourly increments
     */
    private void checkTeHourIncrements(TimeEntry entry) throws TimeRecordErrorException {
        checkHourValueIncrement("workHours", entry.getWorkHours(), "TE", teHourIncrement);
    }

    /**
     * Check non-temporary fields hourly increments
     */
    private void checkRaSaHourIncrements(TimeEntry entry) throws TimeRecordErrorException {
        checkRaSaHourValueIncrement("workHours", entry.getWorkHours());
        checkRaSaHourValueIncrement("holidayHours", entry.getHolidayHours());
        checkRaSaHourValueIncrement("travelHours", entry.getTravelHours());
        checkRaSaHourValueIncrement("personalHours", entry.getPersonalHours());
        checkRaSaHourValueIncrement("vacationHours", entry.getVacationHours());
        checkRaSaHourValueIncrement("sickEmpHours", entry.getSickEmpHours());
        checkRaSaHourValueIncrement("sickFamHours", entry.getSickFamHours());
        checkRaSaHourValueIncrement("miscHours", entry.getMiscHours());
    }

    private void checkRaSaHourValueIncrement(String fieldName, Optional<BigDecimal> fieldValueOpt) {
        checkHourValueIncrement(fieldName, fieldValueOpt, "RA/SA", annualHourIncrement);
    }

    private void checkHourValueIncrement(String fieldName, Optional<BigDecimal> fieldValueOpt,
                                         String category, BigDecimal requiredIncrement) {
        BigDecimal fieldValue = fieldValueOpt.orElse(ZERO);
        if (fieldValue.remainder(requiredIncrement).compareTo(ZERO) != 0) {
            throw new TimeRecordErrorException(INVALID_HOURLY_INCREMENT,
                    new InvalidParameterView(fieldName, "decimal",
                            category + " hours must be in increments of " + requiredIncrement,
                            fieldValue));
        }
    }
}
