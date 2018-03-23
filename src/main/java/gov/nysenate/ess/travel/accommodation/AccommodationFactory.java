package gov.nysenate.ess.travel.accommodation;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import gov.nysenate.ess.travel.utils.Dollars;
import gov.nysenate.ess.travel.application.TravelApplicationView;
import gov.nysenate.ess.travel.application.TravelDestination;
import gov.nysenate.ess.travel.application.TravelDestinationView;
import gov.nysenate.ess.travel.gsa.GsaAllowanceService;
import gov.nysenate.ess.travel.meal.MealTier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class AccommodationFactory {

    private GsaAllowanceService gsaService;

    @Autowired
    public AccommodationFactory(GsaAllowanceService gsaService) {
        this.gsaService = gsaService;
    }

    public List<Accommodation> createAccommodations(TravelApplicationView app) throws IOException {
        List<Accommodation> accommodations = new ArrayList<>();
        for (TravelDestinationView destination : app.getDestinations()) {
            accommodations.add(createAccommodation(destination));
        }
        return accommodations;
    }

    private Accommodation createAccommodation(TravelDestinationView destination) throws IOException {
        TravelDestination dest = destination.toTravelDestination();
        List<Stay> stays = new ArrayList<>();
        stays.addAll(createDateStays(dest));
        stays.addAll(createNightStays(dest));
        return new Accommodation(dest.getAddress(), ImmutableSet.copyOf(stays), dest.isMealsRequested(), dest.isLodgingRequested());
    }

    private List<Stay> createDateStays(TravelDestination dest) throws IOException {
        List<Stay> stays = new ArrayList<>();
        LocalDate dayOfStay = dest.getArrivalDate();
        while (dayRange(dest).contains(dayOfStay)) {
            MealTier tier = gsaService.fetchMealTier(dayOfStay, dest.getAddress());
            stays.add(new DayStay(dayOfStay, tier));
            dayOfStay = dayOfStay.plusDays(1);
        }
        return stays;
    }

    private Range<LocalDate> dayRange(TravelDestination dest) {
        return Range.closed(dest.getArrivalDate(), dest.getDepartureDate());
    }

    private List<Stay> createNightStays(TravelDestination dest) throws IOException {
        List<Stay> stays = new ArrayList<>();
        LocalDate dayOfStay = dest.getArrivalDate().plusDays(1);
        while(nightRange(dest).contains(dayOfStay)) {
            Dollars lodgingRate = gsaService.fetchLodgingRate(dayOfStay, dest.getAddress());
            stays.add(new NightStay(dayOfStay, lodgingRate));
            dayOfStay = dayOfStay.plusDays(1);
        }
        return stays;
    }

    private Range<LocalDate> nightRange(TravelDestination dest) {
        return Range.openClosed(dest.getArrivalDate(), dest.getDepartureDate());
    }
}
