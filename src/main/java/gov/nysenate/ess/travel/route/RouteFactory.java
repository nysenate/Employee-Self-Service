package gov.nysenate.ess.travel.route;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.maps.errors.ApiException;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.TravelApplicationView;
import gov.nysenate.ess.travel.application.TravelDestination;
import gov.nysenate.ess.travel.application.TravelDestinationView;
import gov.nysenate.ess.travel.miles.IrsMileageRateDao;
import gov.nysenate.ess.travel.miles.MileageAllowanceService;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This factory creates a {@link Route} domain object
 * from user entered destination info stored in {@link TravelDestinationView TravelDestinationView's}
 *
 * The Route created is initialized with the IRS reimbursement rate and
 * the driving distances for each part of the trip.
 */
@Component
public class RouteFactory {

    private MileageAllowanceService mileageService;
    private IrsMileageRateDao irsDao;

    @Autowired
    public RouteFactory(MileageAllowanceService mileageService, IrsMileageRateDao irsDao) {
        this.mileageService = mileageService;
        this.irsDao = irsDao;
    }

    public Route createRoute(TravelApplicationView app) throws InterruptedException, ApiException, IOException {
        List<TravelDestination> destinations = app.getDestinations().stream()
                .map(TravelDestinationView::toTravelDestination)
                .collect(Collectors.toList());

        List<Leg> outgoingLegs = outgoingLegs(app.getOrigin().toAddress(), destinations);
        List<Leg> rLegs = returnLegs(app.getOrigin().toAddress(), destinations);

        return new Route(ImmutableList.copyOf(outgoingLegs), ImmutableList.copyOf(rLegs),
                new Dollars(irsDao.getIrsRate(destinations.get(0).getArrivalDate())));
    }

    private List<Leg> outgoingLegs(Address origin, List<TravelDestination> destinations) throws InterruptedException, ApiException, IOException {
        List<Leg> outgoingLegs = new ArrayList<>();
        Address prev = origin;
        for (TravelDestination dest : destinations) {
            Address from = prev;
            Address to = dest.getAddress();
            ModeOfTransportation mot = dest.getModeOfTransportation();
            double miles = mileageService.calculateMileage(from, to);
            Leg leg = new Leg(from, to, miles, mot, dest.isMileageRequested());
            outgoingLegs.add(leg);
            prev = dest.getAddress();
        }
        return outgoingLegs;
    }

    private List<Leg> returnLegs(Address origin, List<TravelDestination> destinations) throws InterruptedException, ApiException, IOException {
        List<TravelDestination> waypoints = returnTripWaypoints(destinations);

        List<Leg> returnLegs = new ArrayList<>();
        TravelDestination from = destinations.get(destinations.size() - 1);
        for (TravelDestination to : waypoints) {
            double miles = mileageService.calculateMileage(from.getAddress(), to.getAddress());
            returnLegs.add(new Leg(from.getAddress(), to.getAddress(), miles,
                    from.getModeOfTransportation(), from.isMileageRequested()));
            from = to;
        }
        // last leg back to origin
        if (waypoints.size() > 0) {
            returnLegs.add(returnToOriginLegFrom(origin, waypoints.get(0)));
        }
        else {
            returnLegs.add(returnToOriginLegFrom(origin, destinations.get(destinations.size() - 1)));
        }
        return returnLegs;
    }

    private Leg returnToOriginLegFrom(Address origin, TravelDestination frm) throws InterruptedException, ApiException, IOException {
        double miles = mileageService.calculateMileage(frm.getAddress(), origin);
        return new Leg(frm.getAddress(), origin, miles, frm.getModeOfTransportation(), frm.isMileageRequested());
    }

    /**
     * Returns, in order to be visited, destinations that need to be visited on the return trip.
     * Destinations need to be visited on the return trip if their mode of transportation changed.
     * @param destinations All destinations in a travel application.
     * @return
     */
    private List<TravelDestination> returnTripWaypoints(List<TravelDestination> destinations) {
        List<TravelDestination> waypoints = new ArrayList<>();
        TravelDestination prevDest = null;
        for (TravelDestination dest : destinations) {
             if (prevDest != null && prevDest.getModeOfTransportation() != dest.getModeOfTransportation()) {
                waypoints.add(prevDest);
            }
            prevDest = dest;
        }
        return Lists.reverse(waypoints);
    }
}
