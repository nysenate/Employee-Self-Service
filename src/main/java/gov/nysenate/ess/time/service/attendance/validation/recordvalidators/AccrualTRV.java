package gov.nysenate.ess.time.service.attendance.validation.recordvalidators;

import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.time.model.accrual.AccrualsAvailable;
import gov.nysenate.ess.time.model.accrual.PeriodAccUsage;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
import gov.nysenate.ess.time.model.attendance.TimeRecordScope;
import gov.nysenate.ess.time.service.accrual.EssAccrualComputeService;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorCode;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorException;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Checks time records to make sure that entered time does not exceed allowed accrual values
 */
@Service
public class AccrualTRV implements TimeRecordValidator {

    private static final Logger logger = LoggerFactory.getLogger(AccrualTRV.class);

    private final EssAccrualComputeService essAccrualComputeService;

    @Autowired
    public AccrualTRV(EssAccrualComputeService essAccrualComputeService) {
        this.essAccrualComputeService = essAccrualComputeService;
    }

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) {
        // If the saved record contains entries where the employee was NOT a temporary employee
        return record.getScope() == TimeRecordScope.EMPLOYEE &&
                record.getTimeEntries().stream()
                        .anyMatch(entry -> entry.getPayType() != PayType.TE);
    }

    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action)
            throws TimeRecordErrorException {

        AccrualsAvailable accrualsAvailable =
                essAccrualComputeService.getAccrualsAvailable(record.getEmployeeId(), record.getPayPeriod());

        PeriodAccUsage recordAccUsage;
        recordAccUsage = record.getPeriodAccUsage();

        // Check personal, vacaction and sick hours

        checkAccrualValue("personalHours",
                recordAccUsage.getPerHoursUsed(), accrualsAvailable.getPersonalAvailable());

        checkAccrualValue("vacationHours",
                recordAccUsage.getVacHoursUsed(), accrualsAvailable.getVacationAvailable());

        checkAccrualValue("sickHours",
                recordAccUsage.getTotalSickHoursUsed(), accrualsAvailable.getSickAvailable());
    }

    /* --- Internal Methods --- */

    /**
     * Test to see if a used accrual value exceeds the available amount
     * If the value exceeds, throw a validation exception
     */
    private void checkAccrualValue(String paramName, BigDecimal recordHours, BigDecimal availableHours)
            throws TimeRecordErrorException {
        recordHours = Optional.ofNullable(recordHours).orElse(BigDecimal.ZERO);

        // Do not check accrual value if it is zero
        // This prevents an error if available accruals are negative for whatever reason,
        // and the user submits a time record with no accrual usage.
        if (BigDecimal.ZERO.compareTo(recordHours) == 0) {
            return;
        }

        if (recordHours.compareTo(availableHours) > 0) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.RECORD_EXCEEDS_ACCRUAL,
                    new InvalidParameterView(paramName, "BigDecimal",
                            "record " + paramName + " may not exceed available " + paramName,
                            recordHours)
            );
        }
    }
}
