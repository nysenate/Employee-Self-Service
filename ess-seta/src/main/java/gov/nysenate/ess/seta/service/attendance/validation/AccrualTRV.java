package gov.nysenate.ess.seta.service.attendance.validation;

import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.seta.model.accrual.PeriodAccSummary;
import gov.nysenate.ess.seta.model.accrual.PeriodAccUsage;
import gov.nysenate.ess.seta.model.attendance.TimeRecord;
import gov.nysenate.ess.seta.model.attendance.TimeRecordScope;
import gov.nysenate.ess.seta.service.accrual.EssAccrualComputeService;
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

    @Override
    public boolean isApplicable(TimeRecord record, Optional<TimeRecord> previousState) {
        // If the saved record contains entries where the employee was a temporary employee
        return record.getScope() == TimeRecordScope.EMPLOYEE &&
                record.getTimeEntries().stream()
                        .anyMatch(entry -> entry.getPayType() != PayType.TE);
    }

    @Override
    public void checkTimeRecord(TimeRecord record, Optional<TimeRecord> previousState) throws TimeRecordErrorException {

        PeriodAccUsage periodAccUsage;
        periodAccUsage = record.getPeriodAccUsage();

        TimeRecord previousTR =  previousState.orElse(null);
        PayPeriod previousPP = previousTR.getPayPeriod();
        logger.info("*Previous Pay Period: {} -  {}", previousPP.getStartDate(), previousPP.getEndDate());
        PeriodAccSummary periodAccSummary;
        periodAccSummary = essAccrualComputeService.getAccruals(record.getEmployeeId(), previousPP);

        BigDecimal perHoursRemain;
        perHoursRemain = periodAccSummary.getPerHoursAccrued()
                            .subtract(periodAccSummary.getPerHoursUsed())
                            .subtract(periodAccUsage.getPerHoursUsed())
                            .add(previousTR.getPeriodAccUsage().getPerHoursUsed());

        BigDecimal vacHoursRemain;
        vacHoursRemain = periodAccSummary.getVacHoursAccrued()
                            .subtract(periodAccSummary.getVacHoursUsed())
                            .subtract(periodAccUsage.getVacHoursUsed()
                            .add(previousTR.getPeriodAccUsage().getVacHoursUsed()));

        BigDecimal sickHoursRemain;
        sickHoursRemain = periodAccSummary.getEmpHoursAccrued()
                            .subtract(periodAccSummary.getEmpHoursUsed())
                            .subtract(periodAccUsage.getEmpHoursUsed())
                            .subtract(periodAccSummary.getFamHoursUsed())
                            .subtract(periodAccUsage.getFamHoursUsed())
                            .add(previousTR.getPeriodAccUsage().getEmpHoursUsed())
                            .add(previousTR.getPeriodAccUsage().getFamHoursUsed())
                            ;

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
