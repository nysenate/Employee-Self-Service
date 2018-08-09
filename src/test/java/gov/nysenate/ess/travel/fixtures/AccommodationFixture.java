package gov.nysenate.ess.travel.fixtures;

import gov.nysenate.ess.travel.utils.Dollars;
import gov.nysenate.ess.travel.provider.gsa.meal.MealTier;

import java.time.LocalDate;

public class AccommodationFixture {

    public static MealTier TIER = MealRatesFixture.mealRatesFor2018().getTier("51");
    public static Dollars LODGING_RATE = new Dollars("120");
    public static LocalDate MONDAY = LocalDate.of(2018, 1, 1);
    public static LocalDate TUESDAY = LocalDate.of(2018, 1, 2);

//    public static Destination twoDayOneNightAccommodation() {
//        return new Destination(new Address(), twoDayStays(), oneNightStay());
//    }
//
//    public static ImmutableSet<Day> twoDayStays() {
//        Day dayOne = new Day(MONDAY, TIER, true);
//        Day dayTwo = new Day(TUESDAY, TIER, true);
//        return ImmutableSet.of(dayTwo, dayOne);
//    }
//
//    public static ImmutableSet<Night> oneNightStay() {
//        Night night = new Night(TUESDAY, LODGING_RATE, true);
//        return ImmutableSet.of(night);
//    }
}
