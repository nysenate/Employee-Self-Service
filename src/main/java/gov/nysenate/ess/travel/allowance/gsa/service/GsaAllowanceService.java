package gov.nysenate.ess.travel.allowance.gsa.service;

import gov.nysenate.ess.travel.allowance.gsa.dao.MealRatesDao;
import gov.nysenate.ess.travel.allowance.gsa.model.*;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
public class GsaAllowanceService {

    private GsaClient client;
    private MealRatesDao mealRatesDao;

    @Autowired
    public GsaAllowanceService(GsaClient client, MealRatesDao mealRatesDao) {
        this.client = client;
        this.mealRatesDao = mealRatesDao;
    }

    /**
     * Calculates the {@link LodgingAllowance} for a {@link Itinerary}.
     * @param itinerary The Itinerary for a travel application.
     * @return
     * @throws IOException
     */
    public LodgingAllowance calculateLodging(Itinerary itinerary) throws IOException {
        LodgingAllowance lodgingAllowance = new LodgingAllowance();
        for (TravelDestination destination: itinerary.getDestinations()) {
            lodgingAllowance = lodgingAllowance.add(lodgingForDestination(destination));
        }
        return lodgingAllowance;
    }

    private LodgingAllowance lodgingForDestination(TravelDestination destination) throws IOException {
        LodgingAllowance allowance = new LodgingAllowance();
        for (LocalDate date: destination.getNightsOfStay()) {
            allowance = allowance.addNight(createLodgingNight(destination, date));
        }
        return allowance;
    }

    private LodgingNight createLodgingNight(TravelDestination destination, LocalDate date) throws IOException {
        GsaResponse res = client.queryGsa(date, destination.getAddress().getZip5());
        return new LodgingNight(date, destination.getAddress(), res.getLodging(date));
    }

    /**
     * Calculates the {@link MealAllowance} for a {@link Itinerary}.
     * @param itinerary
     * @return
     * @throws IOException
     */
    public MealAllowance calculateMealAllowance(Itinerary itinerary) throws IOException {
        MealAllowance allowance = new MealAllowance();
        for (TravelDestination destination: itinerary.getDestinations()) {
            allowance = allowance.add(mealsForDestination(destination));
        }
        return allowance;
    }

    private MealAllowance mealsForDestination(TravelDestination destination) throws IOException {
        MealAllowance allowance = new MealAllowance();
        for (LocalDate date: destination.getDatesOfStay()) {
            allowance = allowance.addMealDay(createMealDay(destination, date));
        }
        return allowance;
    }

    private MealDay createMealDay(TravelDestination destination, LocalDate date) throws IOException {
        GsaResponse res = client.queryGsa(date, destination.getAddress().getZip5());
        String mealTier = res.getMealTier();
        MealRates mealRates = mealRatesDao.getMealRates(date);
        return new MealDay(date, destination.getAddress(), mealRates.getTier(mealTier));
    }
}
