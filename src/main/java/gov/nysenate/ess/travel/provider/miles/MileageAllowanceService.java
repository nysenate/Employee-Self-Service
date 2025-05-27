package gov.nysenate.ess.travel.provider.miles;

import com.google.maps.errors.ApiException;
import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.core.service.notification.slack.service.SlackChatService;
import gov.nysenate.ess.core.util.HttpUtils;
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
    private static final String mileageUrl =
            "https://www.gsa.gov/travel/plan-book/transportation-airfare-rates-pov-rates-etc/privately-owned-vehicle-pov-mileage-reimbursement-rates";

    private final GoogleMapsService googleMapsService;
    private final IrsMileageRateDao irsMileageRateDao;
    private final SlackChatService slackChatService;

    @Autowired
    public MileageAllowanceService(GoogleMapsService googleMapsService, IrsMileageRateDao irsMileageRateDao,
                                   SlackChatService slackChatService) {
        this.googleMapsService = googleMapsService;
        this.irsMileageRateDao = irsMileageRateDao;
        this.slackChatService = slackChatService;
    }

    /**
     * Calculates the driving mileage from one address to another
     *
     * @throws ProviderException if an error is encountered while communicating with our 3rd party distance provider.
     */
    public double drivingDistance(TravelAddress from, TravelAddress to) {
        double distance;
        try {
            distance = googleMapsService.drivingDistance(from, to);
        } catch (InterruptedException | ApiException | IOException ex) {
            throw new ProviderException(ex);
        }
        return distance;
    }

    /**
     * Gets the IRS mileage rate for the given date.
     */
    public BigDecimal getIrsRate(LocalDate date) {
        return irsMileageRateDao.getMileageRate(date).rate();
    }

    public MileageRate scrapeCurrentMileageRate() {
        try {
            MileageRate scrapedRate = MileageRateParser.parseMileageRate(HttpUtils.urlToString(mileageUrl));
            MileageRate currentRate = irsMileageRateDao.getMileageRate(LocalDate.now());
            if (scrapedRate.startDate().isAfter(currentRate.startDate())) {
                irsMileageRateDao.updateEndDate(currentRate.startDate(), scrapedRate.startDate().minusDays(1));
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