package gov.nysenate.ess.travel.application.model;

import gov.nysenate.ess.travel.allowance.gsa.model.GsaAllowance;
import gov.nysenate.ess.travel.utils.TravelAllowanceUtils;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class TravelAllowances {

    private final GsaAllowance gsaAllowance;
    private final BigDecimal mileage;
    private final BigDecimal tolls;
    private final BigDecimal parking;
    private final BigDecimal alternate; // Bus, subway, train
    private final BigDecimal registrationFee;

    public TravelAllowances(GsaAllowance gsaAllowance, BigDecimal mileage,
                            BigDecimal tolls, BigDecimal parking,
                            BigDecimal alternate, BigDecimal registrationFee) {
        checkArgument(checkNotNull(mileage).signum() >= 0);
        checkArgument(checkNotNull(tolls).signum() >= 0);
        checkArgument(checkNotNull(parking).signum() >= 0);
        checkArgument(checkNotNull(alternate).signum() >= 0);
        checkArgument(checkNotNull(registrationFee).signum() >= 0);
        this.gsaAllowance = checkNotNull(gsaAllowance);
        this.mileage = TravelAllowanceUtils.round(mileage);
        this.tolls = TravelAllowanceUtils.round(tolls);
        this.parking = TravelAllowanceUtils.round(parking);
        this.alternate = TravelAllowanceUtils.round(alternate);
        this.registrationFee = TravelAllowanceUtils.round(registrationFee);
    }

    public TravelAllowances(GsaAllowance gsaAllowance, String mileage,
                            String tolls, String parking,
                            String alternate, String registrationFee) {
        this(gsaAllowance, new BigDecimal(mileage), new BigDecimal(tolls),
                new BigDecimal(parking), new BigDecimal(alternate), new BigDecimal(registrationFee));
    }

    public BigDecimal total() {
        return getGsaAllowance().total().add(getMileage()).add(getTolls()).add(getParking())
                .add(getAlternate()).add(getRegistrationFee());
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
    public String toString() {
        return "TravelAllowances{" +
                "gsaAllowance=" + gsaAllowance +
                ", mileage=" + mileage +
                ", tolls=" + tolls +
                ", parking=" + parking +
                ", alternate=" + alternate +
                ", registrationFee=" + registrationFee +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TravelAllowances that = (TravelAllowances) o;

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
