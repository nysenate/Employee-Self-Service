package gov.nysenate.ess.travel.application.model;

import gov.nysenate.ess.travel.allowance.gsa.model.LodgingAllowance;
import gov.nysenate.ess.travel.allowance.gsa.model.MealAllowance;
import gov.nysenate.ess.travel.allowance.mileage.model.MileageAllowance;
import gov.nysenate.ess.travel.utils.UnitUtils;

import java.math.BigDecimal;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class TravelAllowances {

    private final MealAllowance mealAllowance;
    private final LodgingAllowance lodgingAllowance;
    private final MileageAllowance mileageAllowance;
    private final BigDecimal tolls;
    private final BigDecimal parking;
    private final BigDecimal alternate; // Bus, subway, train
    private final BigDecimal registrationFee;

    public TravelAllowances(MealAllowance mealAllowance, LodgingAllowance lodgingAllowance,
                            MileageAllowance mileageAllowance, BigDecimal tolls, BigDecimal parking,
                            BigDecimal alternate, BigDecimal registrationFee) {
        checkArgument(checkNotNull(tolls).signum() >= 0);
        checkArgument(checkNotNull(parking).signum() >= 0);
        checkArgument(checkNotNull(alternate).signum() >= 0);
        checkArgument(checkNotNull(registrationFee).signum() >= 0);
        this.mealAllowance = checkNotNull(mealAllowance);
        this.lodgingAllowance = checkNotNull(lodgingAllowance);
        this.mileageAllowance = checkNotNull(mileageAllowance);
        this.tolls = UnitUtils.roundToHundredth(tolls);
        this.parking = UnitUtils.roundToHundredth(parking);
        this.alternate = UnitUtils.roundToHundredth(alternate);
        this.registrationFee = UnitUtils.roundToHundredth(registrationFee);
    }

    public TravelAllowances(MealAllowance mealAllowance, LodgingAllowance lodgingAllowance,
                            MileageAllowance mileageAllowance, String tolls, String parking,
                            String alternate, String registrationFee) {
        this(mealAllowance, lodgingAllowance, mileageAllowance, new BigDecimal(tolls),
                new BigDecimal(parking), new BigDecimal(alternate), new BigDecimal(registrationFee));
    }

    public BigDecimal total() {
        return mealAllowance.getTotal()
                .add(lodgingAllowance.getTotal())
                .add(mileageAllowance.getAllowance())
                .add(getTolls())
                .add(getParking())
                .add(getAlternate())
                .add(getRegistrationFee());
    }

    public MealAllowance getMealAllowance() {
        return mealAllowance;
    }

    public LodgingAllowance getLodgingAllowance() {
        return lodgingAllowance;
    }

    public MileageAllowance getMileageAllowance() {
        return mileageAllowance;
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
                "mealAllowance=" + mealAllowance +
                ", lodgingAllowance=" + lodgingAllowance +
                ", mileageAllowance=" + mileageAllowance +
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
        return Objects.equals(mealAllowance, that.mealAllowance) &&
                Objects.equals(lodgingAllowance, that.lodgingAllowance) &&
                Objects.equals(mileageAllowance, that.mileageAllowance) &&
                Objects.equals(tolls, that.tolls) &&
                Objects.equals(parking, that.parking) &&
                Objects.equals(alternate, that.alternate) &&
                Objects.equals(registrationFee, that.registrationFee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mealAllowance, lodgingAllowance, mileageAllowance,
                tolls, parking, alternate, registrationFee);
    }
}
