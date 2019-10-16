package gov.nysenate.ess.travel.application.allowances;

import gov.nysenate.ess.travel.utils.Dollars;

import java.util.Objects;

/**
 * A simple data structure used internally to the allowances package to represent a single allowance.
 */
class Allowance {

    protected int allowanceId;
    protected AllowanceType type;
    protected Dollars dollars;

    protected Allowance(AllowanceType type, Dollars dollars) {
        this(0, type, dollars);
    }

    protected Allowance(int allowanceId, AllowanceType type, Dollars dollars) {
        this.allowanceId = allowanceId;
        this.type = type;
        this.dollars = dollars;
    }

    @Override
    public String toString() {
        return "Allowance{" +
                "id=" + allowanceId +
                ", type=" + type +
                ", dollars=" + dollars +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Allowance allowance = (Allowance) o;
        return allowanceId == allowance.allowanceId &&
                type == allowance.type &&
                Objects.equals(dollars, allowance.dollars);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allowanceId, type, dollars);
    }
}
