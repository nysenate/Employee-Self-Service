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

//    public LodgingAllowance calculateLodging(Itinerary itinerary) throws IOException {
//        LodgingAllowance lodgingAllowance = new LodgingAllowance();
//        for (TravelDestination destination: itinerary.getLodgingRequestedDestinations()) {
//            lodgingAllowance = lodgingAllowance.add(lodgingForDestination(destination));
//        }
//        return lodgingAllowance;
//    }
//
//    private LodgingAllowance lodgingForDestination(TravelDestination destination) throws IOException {
//        LodgingAllowance allowance = new LodgingAllowance();
//        for (LocalDate date: destination.getNightsOfStay()) {
//            allowance = allowance.addNight(createLodgingNight(destination, date));
//        }
//        return allowance;
//    }
//
//    private LodgingNight createLodgingNight(TravelDestination destination, LocalDate date) throws IOException {
//        GsaResponse res = client.queryGsa(date, destination.getAddress().getZip5());
//        return new LodgingNight(date, destination.getAddress(), res.getLodging(date));
//    }
//
//    /**
//     * Calculates the {@link MealAllowance} for a {@link Itinerary}.
//     * @param itinerary
//     * @return
//     * @throws IOException
//     */
//    public MealAllowance calculateMealAllowance(Itinerary itinerary) throws IOException {
//        MealAllowance allowance = new MealAllowance();
//        for (TravelDestination destination: itinerary.getMealsRequestedDestinations()) {
//            allowance = allowance.add(mealsForDestination(destination));
//        }
//        return allowance;
//    }
//
//    private MealAllowance mealsForDestination(TravelDestination destination) throws IOException {
//        MealAllowance allowance = new MealAllowance();
//        for (LocalDate date: destination.getDatesOfStay()) {
//            allowance = allowance.addMealDay(createMealDay(destination, date));
//        }
//        return allowance;
//    }
//
//    private MealDay createMealDay(TravelDestination destination, LocalDate date) throws IOException {
//        GsaResponse res = client.queryGsa(date, destination.getAddress().getZip5());
//        MealRates mealRates = mealRatesDao.getMealRates(date);
//        return new MealDay(date, destination.getAddress(), mealRates.getTier(res.getMealTier()));
//    }
}
