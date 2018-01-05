package gov.nysenate.ess.travel.allowance.gsa.service;

import gov.nysenate.ess.travel.allowance.gsa.model.GsaResponse;
import gov.nysenate.ess.travel.allowance.gsa.model.LodgingAllowance;
import gov.nysenate.ess.travel.allowance.gsa.model.LodgingNight;
import gov.nysenate.ess.travel.allowance.gsa.model.MealAllowance;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class GsaAllowanceService {

    private GsaClient client;
    private MealRatesService mealRatesService;

    @Autowired
    public GsaAllowanceService(GsaClient client, MealRatesService mealRatesService) {
        this.client = client;
        this.mealRatesService = mealRatesService;
    }

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
            allowance.addNight(createLodgingNight(destination, date));
        }
        return allowance;
    }

    private LodgingNight createLodgingNight(TravelDestination destination, LocalDate date) throws IOException {
        GsaResponse res = client.queryGsa(date, destination.getAddress().getZip5());
        return new LodgingNight(date, destination.getAddress(), res.getLodging(date));
    }

    public MealAllowance calculateMealAllowance(Itinerary itinerary) throws IOException {
        BigDecimal meals = BigDecimal.ZERO;
        for (TravelDestination destination: itinerary.getDestinations()) {
            meals.add(mealsForDestination(destination));
        }
        return new MealAllowance(meals);
    }

    private BigDecimal mealsForDestination(TravelDestination destination) throws IOException {
        BigDecimal meals = BigDecimal.ZERO;
        for (LocalDate date: destination.getDatesOfStay()) {
            GsaResponse res = client.queryGsa(date, destination.getAddress().getZip5());
            String mealTier = res.getMealTier();
            // TODO: localMealDao.getBreakfast(zip, mealRow);
            // TODO: localMealDao.getDinner(zip, mealRow);
            // meals.add(breakfast, dinner)
        }
        return meals;
    }
}
