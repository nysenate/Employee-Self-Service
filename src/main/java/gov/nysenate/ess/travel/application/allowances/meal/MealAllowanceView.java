package gov.nysenate.ess.travel.application.allowances.meal;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.address.TravelAddressView;
import gov.nysenate.ess.travel.provider.gsa.meal.MealTierView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class MealAllowanceView implements ViewObject {

    String id;
    TravelAddressView address;
    String date;
    MealTierView mealTier;
    @JsonProperty(value="isMealsRequested")
    boolean isMealsRequested;
    String allowance;

    public MealAllowanceView() {
    }

    public MealAllowanceView(MealAllowance mealAllowance) {
        this.id = mealAllowance.getId().toString();
        this.address = new TravelAddressView(mealAllowance.getAddress());
        this.date = mealAllowance.getDate().format(DateTimeFormatter.ISO_DATE);
        this.mealTier = new MealTierView(mealAllowance.getMealTier());
        this.isMealsRequested = mealAllowance.isMealsRequested();
        this.allowance = mealAllowance.allowance().toString();
    }

    public MealAllowance toMealAllowance() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        return new MealAllowance(UUID.fromString(id), address.toTravelAddress(), LocalDate.parse(date, DateTimeFormatter.ISO_DATE),
                mealTier.toMealTier(), isMealsRequested);
    }

    public String getId() {
        return id;
    }

    public String getAllowance() {
        return allowance;
    }

    public AddressView getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public MealTierView getMealTier() {
        return mealTier;
    }

    @JsonProperty(value="isMealsRequested")
    public boolean isMealsRequested() {
        return isMealsRequested;
    }

    @Override
    public String getViewType() {
        return "meal-allowance";
    }
}
