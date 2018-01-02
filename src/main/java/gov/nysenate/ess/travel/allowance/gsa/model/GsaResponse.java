package gov.nysenate.ess.travel.allowance.gsa.model;

import java.math.BigDecimal;
import java.time.Month;
import java.util.Map;
import java.util.Objects;

public final class GsaResponse {

    private final GsaResponseId id;
    private final Map<Month, BigDecimal> lodgingRates;
    private final String mealRow;

    public GsaResponse(GsaResponseId id, Map<Month, BigDecimal> lodgingRates, String mealRow) {
        this.id = id;
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
        return id.getFiscalYear();
    }

    public String getZipcode() {
        return id.getZipcode();
    }

    public GsaResponseId getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GsaResponse that = (GsaResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(lodgingRates, that.lodgingRates) &&
                Objects.equals(mealRow, that.mealRow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lodgingRates, mealRow);
    }
}
