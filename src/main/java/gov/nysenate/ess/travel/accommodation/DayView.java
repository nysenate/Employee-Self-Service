package gov.nysenate.ess.travel.accommodation;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.meal.MealTierView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DayView implements ViewObject {

    public String date;
    public String allowance;
    public MealTierView mealtier;
    public boolean isMealsRequested;

    public DayView() {
    }

    public DayView(Day day) {
        this.date = day.getDate().format(DateTimeFormatter.ISO_DATE);
        this.allowance = day.mealAllowance().toString();
        this.mealtier = new MealTierView(day.getTier());
        this.isMealsRequested = day.isMealsRequested();
    }

    public Day toDay() {
        return new Day(LocalDate.parse(date, DateTimeFormatter.ISO_DATE), mealtier.toMealTier(), isMealsRequested);
    }

    public String getDate() {
        return date;
    }

    public String getAllowance() {
        return allowance;
    }

    public MealTierView getMealtier() {
        return mealtier;
    }

    @JsonProperty(value="isMealsRequested")
    public boolean isMealsRequested() {
        return isMealsRequested;
    }

    @Override
    public String getViewType() {
        return "day";
    }
}
