package gov.nysenate.ess.travel.allowance.gsa.model;

import java.math.BigDecimal;
import java.time.Month;
import java.util.Map;

public final class GsaResponse {

    private final int fiscalYear;
    private final String zipcode;
    private final Map<Month, BigDecimal> lodgingRates;
    private final String mealRow;

    public GsaResponse(int fiscalYear, String zipcode, Map<Month, BigDecimal> lodgingRates, String mealRow) {
        this.fiscalYear = fiscalYear;
        this.zipcode = zipcode;
        this.lodgingRates = lodgingRates;
        this.mealRow = mealRow;
    }

    public BigDecimal getLodging(Month month) {
        return lodgingRates.get(month);
    }

    public String getMealRow() {
        return mealRow;
    }

    public int getFiscalYear() {
        return fiscalYear;
    }

    public String getZipcode() {
        return zipcode;
    }
}
