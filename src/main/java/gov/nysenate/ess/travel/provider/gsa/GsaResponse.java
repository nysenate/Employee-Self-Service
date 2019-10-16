package gov.nysenate.ess.travel.provider.gsa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import java.util.Objects;

public class GsaResponse {

    /** Contains the fiscal year and zip code this response is for. */
    private final GsaResponseId id;
    /** Map of {@link Month} to the lodging rate for that month.*/
    private final Map<Month, BigDecimal> lodgingRates;
    /** The meal and incidental expense tier/row to use for meal rates. */
    private final String mealTier;

    /** These 2 variables are only used when processing batch GSA responses. */
    private String city = "";
    private String county = "";

    public GsaResponse(GsaResponseId id, Map<Month, BigDecimal> lodgingRates, String mealTier) {
        this.id = id;
        this.lodgingRates = lodgingRates;
        this.mealTier = mealTier;
    }

    public GsaResponse(GsaResponseId id, Map<Month, BigDecimal> lodgingRates, String mealTier,
                       String city, String county) {
        this.id = id;
        this.lodgingRates = lodgingRates;
        this.mealTier = mealTier;
        this.city = city;
        this.county = county;
    }

    public BigDecimal getLodging(LocalDate date) {
        return lodgingRates.get(date.getMonth());
    }

    public String getMealTier() {
        return mealTier;
    }

    public GsaResponseId getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public Map<Month, BigDecimal> getLodgingRates() {
        return lodgingRates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GsaResponse that = (GsaResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(lodgingRates, that.lodgingRates) &&
                Objects.equals(mealTier, that.mealTier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lodgingRates, mealTier);
    }
}
