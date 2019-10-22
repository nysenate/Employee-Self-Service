package gov.nysenate.ess.travel.provider.gsa.meal;

import gov.nysenate.ess.travel.utils.Dollars;

import java.util.Objects;

/**
 * A Gsa MIE(meal and incidental expenses). These are the rates used to determine
 * breakfast, dinner, and total meal reimbursements.
 */
public class GsaMie {

    private final int id;
    private final int fiscalYear;
    private final Dollars total;
    private final Dollars gsaBreakfast;
    private final Dollars gsaLunch;
    private final Dollars gsaDinner;
    private final Dollars gsaIncidental;
    private final Dollars gsaFirstLastDay;

    public GsaMie(int id, int fiscalYear, Dollars total, Dollars gsaBreakfast, Dollars gsaLunch,
                  Dollars gsaDinner, Dollars gsaIncidental, Dollars gsaFirstLastDay) {
        this.id = id;
        this.fiscalYear = fiscalYear;
        this.total = total;
        this.gsaBreakfast = gsaBreakfast;
        this.gsaLunch = gsaLunch;
        this.gsaDinner = gsaDinner;
        this.gsaIncidental = gsaIncidental;
        this.gsaFirstLastDay = gsaFirstLastDay;
    }

    /**
     * The Senate's breakfast rate.
     * @return
     */
    public Dollars breakfast() {
        return this.gsaBreakfast;
    }

    /**
     * The Senate's dinner rate. NOT the same as GSA's dinner rate!
     * @return
     */
    public Dollars dinner() {
        return gsaLunch.add(gsaDinner).add(gsaIncidental);
    }

    /**
     * The Senate's daily total meal reimbursement
     * Same as gsaBreakfast + gsaLunch + gsaDinner + gsaIncidental
     * @return
     */
    public Dollars total() {
        return total;
    }

    public int getId() {
        return id;
    }

    public int getFiscalYear() {
        return fiscalYear;
    }

    protected Dollars getGsaBreakfast() {
        return gsaBreakfast;
    }

    protected Dollars getGsaLunch() {
        return gsaLunch;
    }

    protected Dollars getGsaDinner() {
        return gsaDinner;
    }

    protected Dollars getGsaIncidental() {
        return gsaIncidental;
    }

    protected Dollars getGsaFirstLastDay() {
        return gsaFirstLastDay;
    }

    @Override
    public String toString() {
        return "GsaMealRate{" +
                "fiscalYear=" + fiscalYear +
                ", total=" + total +
                ", gsaBreakfast=" + gsaBreakfast +
                ", gsaLunch=" + gsaLunch +
                ", gsaDinner=" + gsaDinner +
                ", gsaIncidental=" + gsaIncidental +
                ", gsaFirstLastDay=" + gsaFirstLastDay +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GsaMie that = (GsaMie) o;
        return fiscalYear == that.fiscalYear &&
                Objects.equals(total, that.total) &&
                Objects.equals(gsaBreakfast, that.gsaBreakfast) &&
                Objects.equals(gsaLunch, that.gsaLunch) &&
                Objects.equals(gsaDinner, that.gsaDinner) &&
                Objects.equals(gsaIncidental, that.gsaIncidental) &&
                Objects.equals(gsaFirstLastDay, that.gsaFirstLastDay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fiscalYear, total, gsaBreakfast, gsaLunch, gsaDinner, gsaIncidental, gsaFirstLastDay);
    }
}
