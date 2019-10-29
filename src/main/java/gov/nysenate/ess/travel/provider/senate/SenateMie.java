package gov.nysenate.ess.travel.provider.senate;

import gov.nysenate.ess.travel.utils.Dollars;

import java.util.Objects;

/**
 * The breakdown of individual meal reimbursements for the Senate.
 * Includes the daily total, breakfast, and dinner reimbursement rates.
 */
public class SenateMie {

    private final int id;
    private final int fiscalYear;
    private final Dollars total;
    private final Dollars breakfast;
    private final Dollars dinner;

    public SenateMie(int id, int fiscalYear, Dollars total, Dollars breakfast, Dollars dinner) {
        this.id = id;
        this.fiscalYear = fiscalYear;
        this.total = total;
        this.breakfast = breakfast;
        this.dinner = dinner;
    }

    /**
     * The Senate's breakfast rate.
     * @return
     */
    public Dollars breakfast() {
        return this.breakfast;
    }

    /**
     * The Senate's dinner rate.
     * @return
     */
    public Dollars dinner() {
        return this.dinner;
    }

    /**
     * The Senate's daily total meal reimbursement
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

    @Override
    public String toString() {
        return "SenateMie{" +
                "id=" + id +
                ", fiscalYear=" + fiscalYear +
                ", total=" + total +
                ", breakfast=" + breakfast +
                ", dinner=" + dinner +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SenateMie senateMie = (SenateMie) o;
        return id == senateMie.id &&
                fiscalYear == senateMie.fiscalYear &&
                Objects.equals(total, senateMie.total) &&
                Objects.equals(breakfast, senateMie.breakfast) &&
                Objects.equals(dinner, senateMie.dinner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fiscalYear, total, breakfast, dinner);
    }
}
