package gov.nysenate.ess.travel.application.model;

import gov.nysenate.ess.travel.allowance.gsa.model.GsaAllowance;
import gov.nysenate.ess.travel.utils.TravelAllowanceUtils;

import java.math.BigDecimal;

public final class TravelAppAllowances {

    private final GsaAllowance gsaAllowance;
    private final BigDecimal mileage;
    private final BigDecimal tolls;
    private final BigDecimal parking;
    private final BigDecimal alternate; // Bus, subway, train
    private final BigDecimal registrationFee;

    public TravelAppAllowances(GsaAllowance gsaAllowance, BigDecimal mileage,
                               BigDecimal tolls, BigDecimal parking,
                               BigDecimal alternate, BigDecimal registrationFee) {
        this.gsaAllowance = gsaAllowance;
        this.mileage = TravelAllowanceUtils.round(mileage);
        this.tolls = TravelAllowanceUtils.round(tolls);
        this.parking = TravelAllowanceUtils.round(parking);
        this.alternate = TravelAllowanceUtils.round(alternate);
        this.registrationFee = TravelAllowanceUtils.round(registrationFee);
    }

    public TravelAppAllowances(GsaAllowance gsaAllowance, String mileage,
                               String tolls, String parking,
                               String alternate, String registrationFee) {
        this.gsaAllowance = gsaAllowance;
        this.mileage = TravelAllowanceUtils.round(new BigDecimal(mileage));
        this.tolls = TravelAllowanceUtils.round(new BigDecimal(tolls));
        this.parking = TravelAllowanceUtils.round(new BigDecimal(parking));
        this.alternate = TravelAllowanceUtils.round(new BigDecimal(alternate));
        this.registrationFee = TravelAllowanceUtils.round(new BigDecimal(registrationFee));
    }

    // TODO only round when returning values??? TESTS!
    public BigDecimal total() {
        return gsaAllowance.total().add(mileage).add(tolls).add(parking)
                .add(alternate).add(registrationFee);
    }

    public GsaAllowance getGsaAllowance() {
        return gsaAllowance;
    }

    public BigDecimal getMileage() {
        return mileage;
    }

    public BigDecimal getTolls() {
        return tolls;
    }

    public BigDecimal getParking() {
        return parking;
    }

    public BigDecimal getAlternate() {
        return alternate;
    }

    public BigDecimal getRegistrationFee() {
        return registrationFee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TravelAppAllowances that = (TravelAppAllowances) o;

        if (gsaAllowance != null ? !gsaAllowance.equals(that.gsaAllowance) : that.gsaAllowance != null) return false;
        if (mileage != null ? !mileage.equals(that.mileage) : that.mileage != null) return false;
        if (tolls != null ? !tolls.equals(that.tolls) : that.tolls != null) return false;
        if (parking != null ? !parking.equals(that.parking) : that.parking != null) return false;
        if (alternate != null ? !alternate.equals(that.alternate) : that.alternate != null) return false;
        return registrationFee != null ? registrationFee.equals(that.registrationFee) : that.registrationFee == null;
    }

    @Override
    public int hashCode() {
        int result = gsaAllowance != null ? gsaAllowance.hashCode() : 0;
        result = 31 * result + (mileage != null ? mileage.hashCode() : 0);
        result = 31 * result + (tolls != null ? tolls.hashCode() : 0);
        result = 31 * result + (parking != null ? parking.hashCode() : 0);
        result = 31 * result + (alternate != null ? alternate.hashCode() : 0);
        result = 31 * result + (registrationFee != null ? registrationFee.hashCode() : 0);
        return result;
    }
}
