package gov.nysenate.ess.travel.provider.miles;

import com.google.maps.errors.ApiException;
import gov.nysenate.ess.core.service.notification.slack.service.SlackChatService;
import gov.nysenate.ess.travel.application.address.GoogleAddress;
import gov.nysenate.ess.travel.provider.ProviderException;
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

    private final GoogleMapsService googleMapsService;
    private final IrsMileageRateDao irsMileageRateDao;
    private final MileageRateScraper mileageRateScraper;
    private final SlackChatService slackChatService;

    @Autowired
    public MileageAllowanceService(GoogleMapsService googleMapsService, IrsMileageRateDao irsMileageRateDao,
                                   MileageRateScraper mileageRateScraper, SlackChatService slackChatService) {
        this.googleMapsService = googleMapsService;
        this.irsMileageRateDao = irsMileageRateDao;
        this.mileageRateScraper = mileageRateScraper;
        this.slackChatService = slackChatService;
    }

    /**
     * Calculates the driving mileage from one address to another
     * @throws ProviderException if an error is encountered while communicating with our 3rd party distance provider.
     */
    public double drivingDistance(GoogleAddress from, GoogleAddress to) {
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
        } catch (Exception e) {
            String msg = "Mileage rate scraping failed! \n" + e;
            logger.error(msg);
            slackChatService.sendMessage(msg);
            return null;
        }
    }

    @Scheduled(cron = "${refresh.mileage.rate.cron:0 0 0 * * *}")
    private void scrapeMileageRate() {
        logger.info("Scraping Mileage Rates...");
        scrapeCurrentMileageRate();
    }
}