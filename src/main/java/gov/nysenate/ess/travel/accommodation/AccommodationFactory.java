package gov.nysenate.ess.travel.accommodation;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.gsa.GsaAllowanceService;
import gov.nysenate.ess.travel.meal.MealTier;
import gov.nysenate.ess.travel.route.Leg;
import gov.nysenate.ess.travel.route.Route;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class AccommodationFactory {

    private GsaAllowanceService gsaService;

    @Autowired
    public AccommodationFactory(GsaAllowanceService gsaService) {
        this.gsaService = gsaService;
    }

    /**
     * Creates Accommodation's from a Route.
     */
    public List<Accommodation> createAccommodations(Route route) throws IOException {
        List<Accommodation> accommodations = new ArrayList<>();
        List<Leg> outboundLegs = route.getOutgoingLegs();
        List<Leg> returnLegs = route.getReturnLegs();
        for (int i = 0; i < outboundLegs.size(); i++) {
            LocalDate arrivalDate = outboundLegs.get(i).getTravelDate();
            LocalDate departureDate = calculateDepartureDate(outboundLegs, returnLegs, i);

            Set<Day> days = createDayStays(outboundLegs.get(i).getTo(), arrivalDate, departureDate);
            Set<Night> nights = createNightStays(outboundLegs.get(i).getTo(), arrivalDate, departureDate);

            Accommodation accommodation = new Accommodation(outboundLegs.get(i).getTo(),
                    ImmutableSet.copyOf(days), ImmutableSet.copyOf(nights));
            accommodations.add(accommodation);
        }
        return accommodations;
    }

    private LocalDate calculateDepartureDate(List<Leg> outboundLegs, List<Leg> returnLegs, int i) {
        LocalDate departureDate;
        if (lastOutboundSegment(outboundLegs, i)) {
            departureDate = returnLegs.get(0).getTravelDate();
        }
        else {
            departureDate = outboundLegs.get(i + 1).getTravelDate();
        }
        return departureDate;
    }

    private boolean lastOutboundSegment(List<Leg> outboundLegs, int i) {
        return i == outboundLegs.size() - 1;
    }

    private Set<Day> createDayStays(Address address, LocalDate arrivalDate, LocalDate departureDate) throws IOException {
        Set<Day> stays = new HashSet<>();
        Range<LocalDate> dayRange = Range.closed(arrivalDate, departureDate);

        LocalDate dayOfStay = arrivalDate;
        while(dayRange.contains(dayOfStay)) {
            MealTier tier = gsaService.fetchMealTier(dayOfStay, address);
            stays.add(new Day(dayOfStay, tier, true));
            dayOfStay = dayOfStay.plusDays(1);
        }
        return stays;
    }

    private Set<Night> createNightStays(Address address, LocalDate arrivalDate, LocalDate departureDate) throws IOException {
        Set<Night> stays = new HashSet<>();
        Range<LocalDate> nightRange = Range.openClosed(arrivalDate, departureDate);

        LocalDate nightOfStay = arrivalDate.plusDays(1);
        while(nightRange.contains(nightOfStay)) {
            Dollars lodgingRate = gsaService.fetchLodgingRate(nightOfStay, address);
            stays.add(new Night(nightOfStay, lodgingRate, true));
            nightOfStay = nightOfStay.plusDays(1);
        }
        return stays;
    }
}
