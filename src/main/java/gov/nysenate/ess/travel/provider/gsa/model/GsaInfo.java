package gov.nysenate.ess.travel.provider.gsa.model;

import java.math.BigDecimal;
import java.time.Month;
import java.util.Map;

public class GsaInfo {
    private String city;
    private String county;
    private Map<Month, BigDecimal> lodgingRates;
    private int fiscalYear;
    private String zipCode;
    private int meals;

    public GsaInfo() {

    }

    public Map<Month, BigDecimal> getLodgingRates() {
        return lodgingRates;
    }

    public void setLodgingRates(Map<Month, BigDecimal> lodgingRates) {
        this.lodgingRates = lodgingRates;
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

    public int getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(int fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public int getMeals() {
        return meals;
    }

    public void setMeals(int meals) {
        this.meals = meals;
    }
}
