package gov.nysenate.ess.travel.provider.dod;

import gov.nysenate.ess.travel.utils.Dollars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
public class DodAllowanceService {

    private static final Logger logger = LoggerFactory.getLogger(DodAllowanceService.class);

    DodService dodService;

    @Autowired
    public DodAllowanceService(DodService dodService) {
        this.dodService = dodService;
    }

    public DodMealTier getDodMealTier(String country, String city, LocalDate travelDate) throws IOException {
        return dodService.getNonConusMealInfo(country, city, travelDate);

    }

    public Dollars fetchMealRate(String country, String city, LocalDate travelDate) throws IOException {
        return new Dollars(getDodMealTier(country, city, travelDate).getTier());
    }

    public Dollars fetchLodgingRate(String country, String city, LocalDate travelDate) throws IOException {
        return getDodMealTier(country, city, travelDate).getLodging();
    }
}
