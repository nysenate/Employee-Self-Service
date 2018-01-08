package gov.nysenate.ess.travel.application.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.allowance.gsa.view.LodgingAllowanceView;
import gov.nysenate.ess.travel.allowance.gsa.view.MealAllowanceView;
import gov.nysenate.ess.travel.application.model.TravelAllowances;

public class TravelAllowancesView implements ViewObject {

    private MealAllowanceView meals;
    private LodgingAllowanceView lodging;
    private String mileage;
    private String tolls;
    private String parking;
    private String alternate;
    private String registrationFee;

    private TravelAllowancesView() {
    }

    public TravelAllowancesView(TravelAllowances allowances) {
        this.meals = new MealAllowanceView(allowances.getMealAllowance());
        this.lodging = new LodgingAllowanceView(allowances.getLodgingAllowance());
        this.mileage = allowances.getMileage().toString();
        this.tolls = allowances.getTolls().toString();
        this.parking = allowances.getParking().toString();
        this.alternate = allowances.getAlternate().toString();
        this.registrationFee = allowances.getRegistrationFee().toString();
    }

    public TravelAllowances toTravelAllowances() {
        return new TravelAllowances(meals.toMealAllowance(), lodging.toLodgingAllowance(),
                mileage, tolls, parking, alternate, registrationFee);
    }

    public MealAllowanceView getMeals() {
        return meals;
    }

    public LodgingAllowanceView getLodging() {
        return lodging;
    }

    public String getMileage() {
        return mileage;
    }

    public String getTolls() {
        return tolls;
    }

    public String getParking() {
        return parking;
    }

    public String getAlternate() {
        return alternate;
    }

    public String getRegistrationFee() {
        return registrationFee;
    }

    @Override
    public String getViewType() {
        return "travel-app-allowances";
    }
}
