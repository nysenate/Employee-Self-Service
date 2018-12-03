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

    private static final Logger logger = LoggerFactory.getLogger(MileageAllowanceService.class);

    @Autowired
    private GoogleMapsService googleMapsService;
    @Autowired
    private IrsMileageRateDao irsMileageRateDao;
    @Autowired
    private DistrictAssignmentService districtAssignmentService;
    @Autowired
    private EmployeeInfoService employeeInfoService;


    /**
     * Calculates the driving mileage from one address to another
     */
    public double drivingDistance(Address from, Address to) throws InterruptedException, ApiException, IOException {
        return googleMapsService.drivingDistance(from, to);
    }

    /**
     * Gets the IRS mileage rate for the given date.
     */
    public BigDecimal getIrsRate(LocalDate date) {
        return irsMileageRateDao.getMileageRate(date).getRate();
    }

    public MileageRate ensureCurrentMileageRate() {
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
            return scrapedRate;
        } catch (IOException e) {
            logger.warn("Mileage rate scraping failed! \n" + e);
            return null;
        }
    }

    @Scheduled(cron = "${cache.cron.mileage.rate:0 0 0 * * *}")
    private void scrapeMileageRate() {
        logger.info("Scraping Mileage Rates...");
        ensureCurrentMileageRate();
    }
}