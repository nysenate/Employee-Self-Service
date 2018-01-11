package gov.nysenate.ess.travel.allowance.mileage.service;

import com.google.maps.errors.ApiException;
import gov.nysenate.ess.core.model.personnel.ResponsibilityCenter;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.addressvalidation.DistrictAssignmentService;
import gov.nysenate.ess.travel.allowance.mileage.dao.IrsRateDao;
import gov.nysenate.ess.travel.allowance.mileage.model.Leg;
import gov.nysenate.ess.travel.allowance.mileage.model.MileageAllowance;
import gov.nysenate.ess.travel.allowance.mileage.model.ReimbursableLeg;
import gov.nysenate.ess.travel.allowance.mileage.model.Route;
import gov.nysenate.ess.travel.application.model.*;
import gov.nysenate.ess.travel.maps.GoogleMapsService;
import gov.nysenate.ess.travel.utils.UnitUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MileageAllowanceService {

    @Autowired
    private GoogleMapsService googleMapsService;

    @Autowired
    private IrsRateDao irsRateDao;

    @Autowired
    private DistrictAssignmentService districtAssignmentService;

    @Autowired
    private EmployeeInfoService employeeInfoService;

    /**
     * Calculates the {@link MileageAllowance} from a {@link Itinerary}.
     * @param itinerary
     * @return
     * @throws InterruptedException
     * @throws ApiException
     * @throws IOException
     */
    public MileageAllowance calculateMileageAllowance(Itinerary itinerary) throws InterruptedException, ApiException, IOException {
        MileageAllowance allowance = new MileageAllowance(getIrsRate(itinerary));
        Route route = itinerary.getReimbursableRoute();
        for (Leg leg : route.getOutboundLegs()) {
            allowance = allowance.addOutboundLeg(calculateReimbursableLeg(leg));
        }
        for (Leg leg : route.getReturnLegs()) {
            allowance = allowance.addReturnLeg(calculateReimbursableLeg(leg));
        }
        return allowance;
    }

    private ReimbursableLeg calculateReimbursableLeg(Leg leg) throws InterruptedException, ApiException, IOException {
        return new ReimbursableLeg(leg, UnitUtils.metersToMiles(googleMapsService.getLegDistance(leg)));
    }

    private BigDecimal getIrsRate(Itinerary itinerary) {
        return BigDecimal.valueOf(irsRateDao.getIrsRate(itinerary.startDate()));
    }

    /**
     * WIP
     *
     * Checks of an employee is traveling outside their district.
     * @param empId
     * @param itinerary
     * @return
     */
    public String leavesDistrict(int empId, Itinerary itinerary) {
        List<Address> travelRoute = itinerary.getDestinations().stream().map(TravelDestination::getAddress).collect(Collectors.toList());

        ResponsibilityCenter respCenter = employeeInfoService.getEmployee(empId).getRespCenter();
        String homeDistString = respCenter.getCode() + "";

        if (homeDistString.charAt(0) != '2') {
            return "This is not a district employee.";
        }
        int homeDist = Integer.parseInt(homeDistString.substring(1));

        for (Address addr : travelRoute) {
            if (districtAssignmentService.assignDistrict(addr).getDistrictNumber() != homeDist) {
                return "This employee is assigned to district " + homeDist + ", and they are leaving that district on this trip.";
            }
        }
        return "This employee is assigned to district " + homeDist + ", and they are not leaving that district on this trip.";
    }
}