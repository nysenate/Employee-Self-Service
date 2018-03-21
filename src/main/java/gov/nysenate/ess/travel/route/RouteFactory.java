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

        // Outgoing legs
        List<Leg> outgoingLegs = new ArrayList();
        Address prev = app.getOrigin().toAddress();
        for (TravelDestination dest : destinations) {
            Address from = prev;
            Address to = dest.getAddress();
            ModeOfTransportation mot = dest.getModeOfTransportation();
            double miles = mileageService.calculateMileage(from, to);
            Leg leg = new Leg(from, to, miles, mot);
            outgoingLegs.add(leg);
            prev = dest.getAddress();
        }

        // TODO: Hackyyy
        // TODO: improve return legs to stop at any destination where mode of transportation changed from Personal Auto.
        TravelDestination lastDest = destinations.get(destinations.size() - 1);
        Address from = lastDest.getAddress();
        Address to = destinations.get(0).getAddress();
        double returnMiles = mileageService.calculateMileage(from, to);
        List<Leg> returnLegs = Lists.newArrayList(new Leg(from, to, returnMiles, lastDest.getModeOfTransportation()));

        return new Route(ImmutableList.copyOf(outgoingLegs), ImmutableList.copyOf(returnLegs),
                new Dollars(irsDao.getIrsRate(lastDest.getArrivalDate())), true); // TODO real impl of isMileaegRequested.
    }
}
