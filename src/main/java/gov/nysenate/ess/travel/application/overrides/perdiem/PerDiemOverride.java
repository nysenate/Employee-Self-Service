package gov.nysenate.ess.travel.application.overrides.perdiem;

import gov.nysenate.ess.travel.utils.Dollars;

import java.util.Objects;

class PerDiemOverride {

    int perDiemOverrideId;
    final PerDiemType type;
    Dollars dollars;

    public PerDiemOverride(PerDiemType type, Dollars dollars) {
        this(0, type, dollars);
    }

    public PerDiemOverride(int perDiemOverrideId, PerDiemType type, Dollars dollars) {
        this.perDiemOverrideId = perDiemOverrideId;
        this.type = type;
        this.dollars = dollars;
    }

    @Override
    public String toString() {
        return "PerDiemOverride{" +
                "perDiemOverrideId=" + perDiemOverrideId +
                ", type=" + type +
                ", dollars=" + dollars +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerDiemOverride that = (PerDiemOverride) o;
        return perDiemOverrideId == that.perDiemOverrideId &&
                type == that.type &&
                Objects.equals(dollars, that.dollars);
    }

    @Override
    public int hashCode() {
        return Objects.hash(perDiemOverrideId, type, dollars);
    }
}
