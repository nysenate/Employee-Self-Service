package gov.nysenate.ess.travel.accommodation;

import com.google.common.collect.ImmutableSet;
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
        LocalDate arrival = dest.getArrivalDate();
        LocalDate departure = dest.getDepartureDate();
        LocalDate tmp = arrival;
        // Add day stays
        while (tmp.isBefore(departure) || tmp.isEqual(departure)) {
            MealTier tier = gsaService.fetchMealTier(tmp, dest.getAddress());
            Stay dayStay = new DayStay(tmp, tier);
            stays.add(dayStay);
            tmp = tmp.plusDays(1);
        }

        // Add night stays
        tmp = arrival.plusDays(1);
        while (tmp.isBefore(departure) || tmp.isEqual(departure)) {
            Dollars lodgingRate = gsaService.fetchLodgingRate(tmp, dest.getAddress());
            Stay nightStay = new NightStay(tmp, lodgingRate);
            stays.add(nightStay);
            tmp = tmp.plusDays(1);
        }

        return new Accommodation(dest.getAddress(), ImmutableSet.copyOf(stays), dest.isMealsRequested(), dest.isLodgingRequested());
    }
}
