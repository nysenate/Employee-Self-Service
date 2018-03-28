package gov.nysenate.ess.travel.gsa;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.utils.Dollars;
import gov.nysenate.ess.travel.gsa.client.GsaClient;
import gov.nysenate.ess.travel.gsa.client.GsaResponse;
import gov.nysenate.ess.travel.meal.MealRates;
import gov.nysenate.ess.travel.meal.MealTier;
import gov.nysenate.ess.travel.meal.SqlMealRatesDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
public class GsaAllowanceService {

    private static final Logger logger = LoggerFactory.getLogger(GsaAllowanceService.class);

    private GsaClient client;
    private SqlMealRatesDao mealRatesDao;

    @Autowired
    public GsaAllowanceService(GsaClient client, SqlMealRatesDao mealRatesDao) {
        this.client = client;
        this.mealRatesDao = mealRatesDao;
    }

    public MealTier fetchMealTier(LocalDate date, Address address) throws IOException {
        GsaResponse res = client.queryGsa(date, address.getZip5());
        MealRates rates = mealRatesDao.getMealRates(date);
        return rates.getTier(res.getMealTier());
    }

    public Dollars fetchLodgingRate(LocalDate date, Address address) throws IOException {
        GsaResponse res = client.queryGsa(date, address.getZip5());
        return new Dollars(res.getLodging(date)); // TODO use dollars in GsaResponse
    }
}
