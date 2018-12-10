package gov.nysenate.ess.travel.application.allowances.mileage;

import gov.nysenate.ess.travel.provider.ProviderException;
import gov.nysenate.ess.travel.provider.miles.MileageAllowanceService;
import gov.nysenate.ess.travel.application.route.Leg;
import gov.nysenate.ess.travel.application.route.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MileageAllowanceFactory {

    private MileageAllowanceService mileageService;

    @Autowired
    public MileageAllowanceFactory(MileageAllowanceService mileageService) {
        this.mileageService = mileageService;
    }

    /**
     * Calculates the mileage allowance from the route.
     * @param route The complete route of travel, must contain outbound and return legs.
     * @return A MileageAllowance initialized from the given Route.
     * @throws ProviderException if an error is encountered while communicating with our 3rd party distance provider.
     */
    public MileageAllowances calculateMileageAllowance(Route route) {
        List<MileageAllowance> outboundLegAllowances = new ArrayList<>();
        List<MileageAllowance> returnLegAllowances = new ArrayList<>();

        for (Leg leg : route.getOutgoingLegs()) {
            if (leg.getModeOfTransportation().qualifiesForMileageReimbursement()) {
                MileageAllowance lma = calculateLegAllowance(leg);
                outboundLegAllowances.add(lma);
            }
        }

        for (Leg leg : route.getReturnLegs()) {
            if (leg.getModeOfTransportation().qualifiesForMileageReimbursement()) {
                MileageAllowance lma = calculateLegAllowance(leg);
                returnLegAllowances.add(lma);
            }
        }

        return new MileageAllowances(outboundLegAllowances, returnLegAllowances);
    }

    private MileageAllowance calculateLegAllowance(Leg leg) {
        double miles = mileageService.drivingDistance(leg.getFrom(), leg.getTo());
        BigDecimal mileageRate = mileageService.getIrsRate(leg.getTravelDate());
        return new MileageAllowance(UUID.randomUUID(), leg, miles, mileageRate);
    }
}
