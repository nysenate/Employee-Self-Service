package gov.nysenate.ess.time.service.attendance.validation;

import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.time.model.accrual.PeriodAccSummary;
import gov.nysenate.ess.time.model.accrual.PeriodAccUsage;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordAction;
import gov.nysenate.ess.time.model.attendance.TimeRecordScope;
import gov.nysenate.ess.time.service.accrual.EssAccrualComputeService;
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
public class AccrualTRV implements TimeRecordValidator {

    private static final Logger logger = LoggerFactory.getLogger(AccrualTRV.class);

    @Autowired private EssAccrualComputeService essAccrualComputeService;
    @Autowired private PayPeriodService payPeriodService;

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action) {
        // If the saved record contains entries where the employee was a temporary employee
        return record.getScope() == TimeRecordScope.EMPLOYEE &&
                record.getTimeEntries().stream()
                        .anyMatch(entry -> entry.getPayType() != PayType.TE);
    }

    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState, TimeRecordAction action)
            throws TimeRecordErrorException {

        PeriodAccUsage periodAccUsage;
        periodAccUsage = record.getPeriodAccUsage();


        PayPeriod previousPP = payPeriodService.getPayPeriod(PayPeriodType.AF, record.getBeginDate().minusDays(1));

        PeriodAccSummary periodAccSummary;
        periodAccSummary = essAccrualComputeService.getAccruals(record.getEmployeeId(), previousPP);

        BigDecimal perHoursRemain;
        perHoursRemain = periodAccSummary.getPerHoursAccrued()
                            .subtract(periodAccSummary.getPerHoursUsed())
                            .subtract(periodAccUsage.getPerHoursUsed());
        BigDecimal vacHoursRemain;
        vacHoursRemain = periodAccSummary.getVacHoursAccrued().add(periodAccSummary.getVacHoursBanked())
                            .subtract(periodAccSummary.getVacHoursUsed())
                            .subtract(periodAccUsage.getVacHoursUsed());

        BigDecimal sickHoursRemain;
        sickHoursRemain = periodAccSummary.getEmpHoursAccrued().add(periodAccSummary.getEmpHoursBanked())
                            .subtract(periodAccSummary.getEmpHoursUsed())
                            .subtract(periodAccUsage.getEmpHoursUsed())
                            .subtract(periodAccSummary.getFamHoursUsed())
                            .subtract(periodAccUsage.getFamHoursUsed());

        //For simple testing only,  below -> only displaying Emp Sick Used

        if (perHoursRemain.signum()==-1) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.RECORD_EXCEEDS_ACCRUAL,
                    new InvalidParameterView("perHoursRemail", "decimal",
                            "perHoursRemain = " + perHoursRemain.toString(), perHoursRemain.toString()));

        }

        if (vacHoursRemain.signum()==-1) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.RECORD_EXCEEDS_ACCRUAL,
                    new InvalidParameterView("vacHoursRemain", "decimal",
                            "vacHoursRemain = " + vacHoursRemain.toString(), vacHoursRemain.toString()));

        }


        if (sickHoursRemain.signum()==-1) {
            throw new TimeRecordErrorException(TimeRecordErrorCode.RECORD_EXCEEDS_ACCRUAL,
                    new InvalidParameterView("sickHoursRemain", "decimal",
                            "sickHoursRemain = " + sickHoursRemain.toString(), sickHoursRemain.toString()));

        } // Commented out for testing


    }
}
