package gov.nysenate.ess.travel.provider.miles;

import com.google.maps.errors.ApiException;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.provider.ProviderException;
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
    @Autowired
    private MileageRateScraper mileageRateScraper;

    /**
     * Calculates the driving mileage from one address to another
     * @throws ProviderException if an error is encountered while communicating with our 3rd party distance provider.
     */
    public double drivingDistance(Address from, Address to) {
        double distance;
        try {
            distance = googleMapsService.drivingDistance(from, to);
        }
        catch (InterruptedException|ApiException|IOException ex) {
            throw new ProviderException(ex);
        }
        return distance;
    }

    /**
     * Gets the IRS mileage rate for the given date.
     */
    public BigDecimal getIrsRate(LocalDate date) {
        return irsMileageRateDao.getMileageRate(date).getRate();
    }

    public MileageRate scrapeCurrentMileageRate() {
        try {
            MileageRate scrapedRate = mileageRateScraper.scrapeMileRates();
            MileageRate currentRate = irsMileageRateDao.getMileageRate(LocalDate.now());
            if (scrapedRate.getStartDate().isAfter(currentRate.getStartDate())) {
                irsMileageRateDao.updateEndDate(currentRate.getStartDate(), scrapedRate.getStartDate().minusDays(1));
                irsMileageRateDao.insertIrsRate(scrapedRate);
                logger.info("IRS mileage rates have been updated.");
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
        scrapeCurrentMileageRate();
    }
}