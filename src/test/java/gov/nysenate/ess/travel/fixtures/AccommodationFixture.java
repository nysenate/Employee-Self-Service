package gov.nysenate.ess.travel.fixtures;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.utils.Dollars;
import gov.nysenate.ess.travel.accommodation.Accommodation;
import gov.nysenate.ess.travel.accommodation.DayStay;
import gov.nysenate.ess.travel.accommodation.NightStay;
import gov.nysenate.ess.travel.accommodation.Stay;
import gov.nysenate.ess.travel.meal.MealTier;

import java.time.LocalDate;

public class AccommodationFixture {

    private static MealTier TIER = MealRatesFixture.mealRatesFor2018().getTier("51");
    private static Dollars LODGING_RATE = new Dollars("120");
    private static LocalDate MONDAY = LocalDate.of(2018, 1, 1);
    private static LocalDate TUESDAY = LocalDate.of(2018, 1, 2);

    public static Accommodation twoDayOneNightAccommodation() {
        ImmutableSet<Stay> stays = twoDayOneNightStays();
        return new Accommodation(new Address(), stays);
    }

    public static ImmutableSet<Stay> twoDayOneNightStays() {
        Stay dayOne = new DayStay(MONDAY, TIER);
        Stay nightOne = new NightStay(TUESDAY, LODGING_RATE);
        Stay dayTwo = new DayStay(TUESDAY, TIER);
        return ImmutableSet.of(dayOne, nightOne, dayTwo);
    }
}
