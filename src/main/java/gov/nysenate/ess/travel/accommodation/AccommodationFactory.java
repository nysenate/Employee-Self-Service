package gov.nysenate.ess.travel.accommodation;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.*;
import gov.nysenate.ess.travel.gsa.GsaAllowanceService;
import gov.nysenate.ess.travel.meal.MealTier;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This factory creates {@link Accommodation} domain objects
 * from user entered data stored in {@link TravelDestinationView TravelDestinationView's}
 *
 * As part of this Accommodation initialization, this factory queries
 * GSA API endpoints to get meal and lodging data as necessary.
 */
@Component
public class AccommodationFactory {

    private GsaAllowanceService gsaService;

    @Autowired
    public AccommodationFactory(GsaAllowanceService gsaService) {
        this.gsaService = gsaService;
    }

    public List<Accommodation> createAccommodations(TravelApplicationView app) throws IOException {
        List<Accommodation> accommodations = new ArrayList<>();
        List<Segment> outboundSegments = app.getOutboundSegments().stream().map(SegmentView::toSegment).collect(Collectors.toList());
        List<Segment> returnSegments = app.getReturnSegments().stream().map(SegmentView::toSegment).collect(Collectors.toList());
        for (int i = 0; i < outboundSegments.size(); i++) {
            LocalDate arrivalDate = outboundSegments.get(i).getArrivalDate();
            LocalDate departureDate = calculateDepartureDate(outboundSegments, returnSegments, i);
            Set<Stay> stays = createDayStays(outboundSegments.get(i).getTo(), arrivalDate, departureDate);
            stays.addAll(createNightStays(outboundSegments.get(i).getTo(), arrivalDate, departureDate));

            Accommodation accommodation = new Accommodation(outboundSegments.get(i).getTo(), ImmutableSet.copyOf(stays),
                    outboundSegments.get(i).isMealsRequested(), outboundSegments.get(i).isLodgingRequested());
            accommodations.add(accommodation);
        }
        return accommodations;
    }

    private LocalDate calculateDepartureDate(List<Segment> outboundSegments, List<Segment> returnSegments, int i) {
        LocalDate departureDate;
        if (lastOutboundSegment(outboundSegments, i)) {
            departureDate = returnSegments.get(0).getDepartureDate();
        }
        else {
            departureDate = outboundSegments.get(i + 1).getDepartureDate();
        }
        return departureDate;
    }

    private boolean lastOutboundSegment(List<Segment> outboundSegments, int i) {
        return i == outboundSegments.size() - 1;
    }

//    private Accommodation createAccommodation(TravelDestinationView destination) throws IOException {
//        TravelDestination dest = destination.toTravelDestination();
//        List<Stay> stays = new ArrayList<>();
//        stays.addAll(createDayStays(dest));
//        stays.addAll(createNightStays(dest));
//        return new Accommodation(dest.getAddress(), ImmutableSet.copyOf(stays), dest.isMealsRequested(), dest.isLodgingRequested());
//    }

    private Set<Stay> createDayStays(Address address, LocalDate arrivalDate, LocalDate departureDate) throws IOException {
        Set<Stay> stays = new HashSet<>();
        Range<LocalDate> dayRange = Range.closed(arrivalDate, departureDate);

        LocalDate dayOfStay = arrivalDate;
        while(dayRange.contains(dayOfStay)) {
            MealTier tier = gsaService.fetchMealTier(dayOfStay, address);
            stays.add(new DayStay(dayOfStay, tier));
            dayOfStay = dayOfStay.plusDays(1);
        }
        return stays;
    }

    private Set<Stay> createNightStays(Address address, LocalDate arrivalDate, LocalDate departureDate) throws IOException {
        Set<Stay> stays = new HashSet<>();
        Range<LocalDate> nightRange = Range.openClosed(arrivalDate, departureDate);

        LocalDate nightOfStay = arrivalDate.plusDays(1);
        while(nightRange.contains(nightOfStay)) {
            Dollars lodgingRate = gsaService.fetchLodgingRate(nightOfStay, address);
            stays.add(new NightStay(nightOfStay, lodgingRate));
            nightOfStay = nightOfStay.plusDays(1);
        }
        return stays;
    }
}
