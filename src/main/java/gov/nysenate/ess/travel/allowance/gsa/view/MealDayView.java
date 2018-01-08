package gov.nysenate.ess.travel.allowance.gsa.view;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.allowance.gsa.model.MealDay;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.*;

public class MealDayView implements ViewObject {

    private String date;
    private AddressView address;
    private MealTierView mealTier;
    // The max rate reimbursed by the Senate for this day.
    private String senateRate;

    public MealDayView() {
    }

    public MealDayView(MealDay mealDay) {
        this.date = mealDay.getDate().format(ISO_DATE);
        this.address = new AddressView(mealDay.getAddress());
        this.mealTier = new MealTierView(mealDay.getTier());
        this.senateRate = mealDay.getSenateRate().toString();
    }

    public MealDay toMealDay() {
        return new MealDay(LocalDate.parse(getDate(), ISO_DATE),
                address.toAddress(), mealTier.toMealTier());
    }

    public String getDate() {
        return date;
    }

    public AddressView getAddress() {
        return address;
    }

    public MealTierView getMealTier() {
        return mealTier;
    }

    public String getSenateRate() {
        return senateRate;
    }

    @Override
    public String getViewType() {
        return "meal-day";
    }
}
