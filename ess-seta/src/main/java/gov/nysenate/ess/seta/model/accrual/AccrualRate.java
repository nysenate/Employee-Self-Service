package gov.nysenate.ess.seta.model.accrual;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides accrual rates for vacation and sick time based on number of biweekly pay periods.
 * These rates apply only to regular annual employees.
 */
public enum AccrualRate
{
    /** Vacation rates increase as you work longer until you reach 5.5 */
    VACATION (Arrays.asList(new BigDecimal(0), new BigDecimal("31.5"), new BigDecimal("3.5"),
                            new BigDecimal("3.75"), new BigDecimal("4"), new BigDecimal("5.5")),
              new BigDecimal("210")),

    /** Sick rates are fixed at 3.5 */
    SICK     (Arrays.asList(new BigDecimal("3.5"), new BigDecimal("3.5"), new BigDecimal("3.5"),
                            new BigDecimal("3.5"), new BigDecimal("3.5"), new BigDecimal("3.5")),
              new BigDecimal("1400"));

    private ArrayList<BigDecimal> accRates;
    private BigDecimal maxHoursBanked;

    AccrualRate(List<BigDecimal> accRates, BigDecimal maxHoursBanked) {
        assert accRates.size() == 6;
        this.accRates = new ArrayList<>(accRates);
        this.maxHoursBanked = maxHoursBanked;
    }

    /**
     * Retrieve the accrual rate based on the payPeriods.
     *
     * @param payPeriods int
     * @return BigDecimal with rate stored
     */
    public BigDecimal getRate(int payPeriods) {
        if (payPeriods <= 12) return accRates.get(0);
        if (payPeriods == 13) return accRates.get(1);
        if (payPeriods >= 14 && payPeriods <= 26) return accRates.get(2);
        if (payPeriods >= 27 && payPeriods <= 52) return accRates.get(3);
        if (payPeriods >= 53 && payPeriods <= 78) return accRates.get(4);
        else return accRates.get(5);
    }

    /**
     * Retrieves the rate using a prorated percentage (occurs when one does not
     * work 1820 hours and accrue at a rate proportional to the number of hours
     * they are expected to work in a year).
     *
     * @param payPeriods int
     * @param proratePercentage BigDecimal (percentage e.g 0.5)
     * @return BigDecimal with prorated accrual rate to the nearest .25.
     */
    public BigDecimal getRate(int payPeriods, BigDecimal proratePercentage) {
        return roundUpToNearestQuarter(getRate(payPeriods).multiply(proratePercentage));
    }

    /**
     * Returns the maximum number of hours that can be rolled over to the next year.
     * @return BigDecimal
     */
    public BigDecimal getMaxHoursBanked() {
        return maxHoursBanked;
    }

    /**
     * Rounds the given BigDecimal up to the nearest .25 increment.
     *
     * @param num BigDecimal
     * @return BigDecimal with rounded value
     */
    private BigDecimal roundUpToNearestQuarter(BigDecimal num) {
        BigDecimal four = new BigDecimal(4);
        return num.multiply(four).setScale(0, RoundingMode.CEILING).divide(four);
    }
}
