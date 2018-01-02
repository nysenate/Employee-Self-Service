package gov.nysenate.ess.travel.allowance.gsa.model;

import java.util.Objects;

public final class GsaResponseId {

    public final int fiscalYear;
    public final String zipcode;

    public GsaResponseId(int fiscalYear, String zipcode) {
        this.fiscalYear = fiscalYear;
        this.zipcode = zipcode;
    }

    public int getFiscalYear() {
        return fiscalYear;
    }

    public String getZipcode() {
        return zipcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GsaResponseId that = (GsaResponseId) o;
        return fiscalYear == that.fiscalYear &&
                Objects.equals(zipcode, that.zipcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fiscalYear, zipcode);
    }
}
