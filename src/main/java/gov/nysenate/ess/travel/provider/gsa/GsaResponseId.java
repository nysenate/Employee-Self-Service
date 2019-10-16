package gov.nysenate.ess.travel.provider.gsa;

import gov.nysenate.ess.core.util.DateUtils;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Uniquely identifies a response from the GSA Zip and Year API endpoint
 */
public class GsaResponseId {

    private final int fiscalYear;
    private final String zipcode;

    public GsaResponseId(LocalDate date, String zipcode) {
        this(DateUtils.getFederalFiscalYear(date), zipcode);
    }

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
