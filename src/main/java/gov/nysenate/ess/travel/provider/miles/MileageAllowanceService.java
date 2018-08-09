package gov.nysenate.ess.travel.provider.miles;

import com.google.maps.errors.ApiException;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.provider.addressvalidation.DistrictAssignmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;


@Service
public class MileageAllowanceService {

    @Autowired
    private GoogleMapsService googleMapsService;

    @Autowired
    private IrsMileageRateDao irsMileageRateDao;

    @Autowired
    private DistrictAssignmentService districtAssignmentService;

    @Autowired
    private EmployeeInfoService employeeInfoService;

    private static final Logger logger = LoggerFactory.getLogger(MileageAllowanceService.class);

    public double calculateMileage(Address from, Address to) throws InterruptedException, ApiException, IOException {
        return googleMapsService.drivingDistance(from, to);
    }

    // TODO Use Dollars
    public BigDecimal getIrsRate(LocalDate date) {
        return irsMileageRateDao.getMileageRate(date).getRate();
    }

    public void ensureCurrentMileageRate() {
        MileageRateScraper scraper = new MileageRateScraper();
        try {
            MileageRate scrapedRate = scraper.scrapeMileRates();
            MileageRate currentRate = irsMileageRateDao.getMileageRate(LocalDate.now());
            if (!scrapedRate.getStartDate().isEqual(currentRate.getStartDate())) {
                irsMileageRateDao.updateEndDate(currentRate.getStartDate(), scrapedRate.getStartDate().minusDays(1));
                irsMileageRateDao.insertIrsRate(scrapedRate);
            } else {
                logger.info("The mileage rate did not change. No updates were made to the database");
            }
        } catch (IOException e) {
            logger.warn("Mileage rate scraping failed! \n" + e);
        }
    }

    @Scheduled(cron = "${cache.cron.mileage.rate:0 0 0 * * *}")
    private void cacheMileageRate() {
        logger.info("Scraping Mileage Rates...");
        ensureCurrentMileageRate();
    }

//    /**
//     * Calculates the {@link MileageAllowance} from a {@link Itinerary}.
//     * @param itinerary
//     * @return
//     * @throws InterruptedException
//     * @throws ApiException
//     * @throws IOException
//     */
//    public MileageAllowance calculateMileageAllowance(Itinerary itinerary) throws InterruptedException, ApiException, IOException {
//        MileageAllowance allowance = new MileageAllowance(getIrsRate(itinerary));
//        Route route = itinerary.getReimbursableRoute();
//        for (Leg leg : route.getOutboundLegs()) {
//            allowance = allowance.addOutboundLeg(calculateReimbursableLeg(leg));
//        }
//        for (Leg leg : route.getReturnLegs()) {
//            allowance = allowance.addReturnLeg(calculateReimbursableLeg(leg));
//        }
//        return allowance;
//    }
//
//    private ReimbursableLeg calculateReimbursableLeg(Leg leg) throws InterruptedException, ApiException, IOException {
//        return new ReimbursableLeg(leg, UnitUtils.metersToMiles(googleMapsService.getLegDistance(leg)));
//    }
//
//    private BigDecimal getIrsRate(Itinerary itinerary) {
//        return irsMileageRateDao.getIrsRate(itinerary.startDate());
//    }
//
//    /**
//     * WIP
//     *
//     * Checks of an employee is traveling outside their district.
//     * @param empId
//     * @param itinerary
//     * @return
//     */
//    public String leavesDistrict(int empId, Itinerary itinerary) {
//        List<Address> travelRoute = itinerary.getDestinations().stream().map(TravelDestination::getAddress).collect(Collectors.toList());
//
//        ResponsibilityCenter respCenter = employeeInfoService.getEmployee(empId).getRespCenter();
//        String homeDistString = respCenter.getCode() + "";
//
//        if (homeDistString.charAt(0) != '2') {
//            return "This is not a district employee.";
//        }
//        int homeDist = Integer.parseInt(homeDistString.substring(1));
//
//        for (Address addr : travelRoute) {
//            if (districtAssignmentService.assignDistrict(addr).getDistrictNumber() != homeDist) {
//                return "This employee is assigned to district " + homeDist + ", and they are leaving that district on this trip.";
//            }
//        }
//        return "This employee is assigned to district " + homeDist + ", and they are not leaving that district on this trip.";
//    }
}