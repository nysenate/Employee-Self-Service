package gov.nysenate.ess.travel.application.model;

import gov.nysenate.ess.travel.allowance.gsa.model.LodgingAllowance;
import gov.nysenate.ess.travel.allowance.gsa.model.MealAllowance;
import gov.nysenate.ess.travel.utils.UnitUtils;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class TravelAllowances {

    private final MealAllowance mealAllowance;
    private final LodgingAllowance lodgingAllowance;
    private final BigDecimal mileage;
    private final BigDecimal tolls;
    private final BigDecimal parking;
    private final BigDecimal alternate; // Bus, subway, train
    private final BigDecimal registrationFee;

    public TravelAllowances(MealAllowance mealAllowance, LodgingAllowance lodgingAllowance,
                            BigDecimal mileage, BigDecimal tolls, BigDecimal parking,
                            BigDecimal alternate, BigDecimal registrationFee) {
        checkArgument(checkNotNull(mileage).signum() >= 0);
        checkArgument(checkNotNull(tolls).signum() >= 0);
        checkArgument(checkNotNull(parking).signum() >= 0);
        checkArgument(checkNotNull(alternate).signum() >= 0);
        checkArgument(checkNotNull(registrationFee).signum() >= 0);
        this.mealAllowance = checkNotNull(mealAllowance);
        this.lodgingAllowance = checkNotNull(lodgingAllowance);
        this.mileage = UnitUtils.roundToHundredth(mileage);
        this.tolls = UnitUtils.roundToHundredth(tolls);
        this.parking = UnitUtils.roundToHundredth(parking);
        this.alternate = UnitUtils.roundToHundredth(alternate);
        this.registrationFee = UnitUtils.roundToHundredth(registrationFee);
    }

    public TravelAllowances(MealAllowance mealAllowance, LodgingAllowance lodgingAllowance,
                            String mileage, String tolls, String parking,
                            String alternate, String registrationFee) {
        this(mealAllowance, lodgingAllowance, new BigDecimal(mileage), new BigDecimal(tolls),
                new BigDecimal(parking), new BigDecimal(alternate), new BigDecimal(registrationFee));
    }

    public BigDecimal total() {
        return mealAllowance.getTotal().add(lodgingAllowance.getTotal()).add(getMileage())
                .add(getTolls()).add(getParking()).add(getAlternate()).add(getRegistrationFee());
    }

    public MealAllowance getMealAllowance() {
        return mealAllowance;
    }

    public LodgingAllowance getLodgingAllowance() {
        return lodgingAllowance;
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
}
