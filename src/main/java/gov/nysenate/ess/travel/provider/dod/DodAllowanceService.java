package gov.nysenate.ess.travel.provider.dod;

import gov.nysenate.ess.travel.utils.Dollars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDate;

public class DodAllowanceService {

    private static final Logger logger = LoggerFactory.getLogger(DodAllowanceService.class);

    DodService dodService;

    @Autowired
    public DodAllowanceService(DodService dodService) {
        this.dodService = dodService;
    }

    public DodMealTier getDodMealTier(String country, String city, LocalDate travelDate) {
        try {
            return dodService.getNonConusMealInfo(country, city, travelDate);
        }
        catch (IOException e) {
            logger.error("Unable to get DOD Meal rates for " + city + ", " + country + " for " + travelDate);
            return null;
        }
    }

    public Dollars fetchMealRate(String country, String city, LocalDate travelDate) {
        return new Dollars( getDodMealTier(country, city, travelDate).getTier() );
    }

    public Dollars fetchLodgingRate(String country, String city, LocalDate travelDate) {
        return getDodMealTier(country, city, travelDate).getLodging();
    }
}
