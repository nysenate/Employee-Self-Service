package gov.nysenate.ess.web.service.attendance.validation;

import gov.nysenate.ess.web.model.allowances.AllowanceUsage;
import gov.nysenate.ess.web.model.attendance.TimeRecord;
import gov.nysenate.ess.web.model.attendance.TimeRecordScope;
import gov.nysenate.ess.web.service.allowance.AllowanceService;
import gov.nysenate.ess.web.client.view.error.InvalidParameterView;
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
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState) {
        // If the saved record contains entries where the employee was a temporary employee
        return record.getRecordStatus().getScope() == TimeRecordScope.EMPLOYEE &&
                record.getTimeEntries().stream()
                        .anyMatch(entry -> entry.getPayType() == PayType.TE);
    }

    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState) throws TimeRecordErrorException {
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
