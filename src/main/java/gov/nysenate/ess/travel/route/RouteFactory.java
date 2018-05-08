package gov.nysenate.ess.travel.route;

import com.google.common.collect.ImmutableList;
import com.google.maps.errors.ApiException;
import gov.nysenate.ess.travel.miles.IrsMileageRateDao;
import gov.nysenate.ess.travel.miles.MileageAllowanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
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

    /**
     * Initializes a route with mileage for each leg and the IRS mileage reimbursement rate.
     */
    public Route initRoute(Route route) throws InterruptedException, ApiException, IOException {
        List<Leg> outboundLegs = new ArrayList<>();
        List<Leg> returnLegs = new ArrayList<>();

        for (Leg leg : route.getOutgoingLegs()) {
            double miles = mileageService.calculateMileage(leg.getFrom(), leg.getTo());
            outboundLegs.add(new Leg(leg.getFrom(), leg.getTo(), miles, leg.getModeOfTransportation(),
                    leg.getTravelDate(), leg.isMileageRequested()));
        }

        for (Leg leg : route.getReturnLegs()) {
            double miles = mileageService.calculateMileage(leg.getFrom(), leg.getTo());
            returnLegs.add(new Leg(leg.getFrom(), leg.getTo(), miles, leg.getModeOfTransportation(),
                    leg.getTravelDate(), leg.isMileageRequested()));
        }
        return new Route(ImmutableList.copyOf(outboundLegs), ImmutableList.copyOf(returnLegs),
                irsDao.getIrsRate(outboundLegs.get(0).getTravelDate()));
    }
}
