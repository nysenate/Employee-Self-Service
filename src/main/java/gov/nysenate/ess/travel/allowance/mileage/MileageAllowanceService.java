package gov.nysenate.ess.travel.allowance.mileage;

import gov.nysenate.ess.core.model.personnel.ResponsibilityCenter;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.addressvalidation.DistrictAssignmentService;
import gov.nysenate.ess.travel.application.model.*;
import gov.nysenate.ess.travel.maps.MapInterface;
import gov.nysenate.ess.travel.maps.MapsService;
import gov.nysenate.ess.travel.maps.OsrmService;
import gov.nysenate.ess.travel.maps.TripDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import java.util.ArrayList;

@Service
public class MileageAllowanceService {

    @Autowired
    private MapsService mapsService;

    @Autowired
    private OsrmService osrmService;

    @Autowired
    private IrsRateDao irsRateDao;

    @Autowired
    private DistrictAssignmentService districtAssignmentService;

    @Autowired
    private EmployeeInfoService employeeInfoService;

    public BigDecimal calculateMileageAllowance(Itinerary itinerary) {
        List<Address> travelRoute = itinerary.travelRoute();

        MapInterface mapInterface = mapsService;
        TripDistance tripDistance;

        try {
            tripDistance = mapInterface.getTripDistance(travelRoute);
        } catch(Exception e) {
            mapInterface = osrmService;
            try {
                tripDistance = mapInterface.getTripDistance(travelRoute);
            } catch(Exception e1) {
                e1.printStackTrace();
                return BigDecimal.ZERO;
            }
        }

        BigDecimal mileageAllowance = new BigDecimal(0);
        if (tripDistance.getTripDistanceOut() > 35) {
            mileageAllowance = BigDecimal.valueOf(tripDistance.getTripDistanceTotal()).multiply(BigDecimal.valueOf(irsRateDao.getIrsRate() / 100));
        }

        return mileageAllowance;
    }

    public String leavesDistrict(int empId, Itinerary itinerary) {
        List<Address> travelRoute = itinerary.travelRoute();

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