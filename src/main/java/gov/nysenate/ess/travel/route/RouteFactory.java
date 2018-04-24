package gov.nysenate.ess.travel.route;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.maps.errors.ApiException;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.*;
import gov.nysenate.ess.travel.miles.IrsMileageRateDao;
import gov.nysenate.ess.travel.miles.MileageAllowanceService;
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
        List<Segment> outboundSegments = app.getOutboundSegments().stream().map(SegmentView::toSegment).collect(Collectors.toList());
        List<Segment> returnSegments = app.getReturnSegments().stream().map(SegmentView::toSegment).collect(Collectors.toList());

        List<Leg> outgoingLegs = new ArrayList<>();
        List<Leg> returnLegs = new ArrayList<>();

        for (Segment s : outboundSegments) {
            double miles = mileageService.calculateMileage(s.getFrom(), s.getTo());
            outgoingLegs.add(new Leg(s.getFrom(), s.getTo(), miles, s.getModeOfTransportation(), s.isMileageRequested()));
        }

        for (Segment s : returnSegments) {
            double miles = mileageService.calculateMileage(s.getFrom(), s.getTo());
            returnLegs.add(new Leg(s.getFrom(), s.getTo(), miles, s.getModeOfTransportation(), s.isMileageRequested()));
        }

        return new Route(ImmutableList.copyOf(outgoingLegs), ImmutableList.copyOf(returnLegs),
                irsDao.getIrsRate(outboundSegments.get(0).getDepartureDate()));
    }
}
