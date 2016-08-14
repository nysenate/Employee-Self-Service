package gov.nysenate.ess.time.service.attendance.validation;

import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.time.model.allowances.AllowanceUsage;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
import gov.nysenate.ess.time.model.attendance.TimeRecordScope;
import gov.nysenate.ess.time.service.allowance.AllowanceService;
import gov.nysenate.ess.core.model.payroll.PayType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Checks time records to make sure that no time record contains time entry that exceeds the employee's yearly allowance
 */
@Service
public class AllowanceTRV implements TimeRecordValidator {

    private static final Logger logger = LoggerFactory.getLogger(AllowanceTRV.class);

    @Autowired private AllowanceService allowanceService;

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) {
        // If the saved record contains entries where the employee was a temporary employee
        return record.getScope() == TimeRecordScope.EMPLOYEE &&
                record.getTimeEntries().stream()
                        .anyMatch(entry -> entry.getPayType() == PayType.TE);
    }

    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action)
            throws TimeRecordErrorException {
        AllowanceUsage allowanceUsage =
                allowanceService.getAllowanceUsage(record.getEmployeeId(), record.getBeginDate().getYear());

        // Get the money used for the current year
        BigDecimal moneyUsed = allowanceUsage.getMoneyUsed();

        // Get money available for this record
        BigDecimal moneyAvailable = allowanceUsage.getYearlyAllowance().subtract(moneyUsed);

        // Get money that would be spent by the new record
        BigDecimal recordCost = allowanceUsage.getRecordCost(record);

        logger.info("avail: {}\tcost: {}", moneyAvailable, recordCost);

        if (recordCost.compareTo(moneyAvailable) > 0) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.RECORD_EXCEEDS_ALLOWANCE,
                    new InvalidParameterView("recordMoneyUsed", "decimal",
                            "recordMoneyUsed <= " + moneyAvailable.toString(), recordCost.toString()));
        }
    }
}
