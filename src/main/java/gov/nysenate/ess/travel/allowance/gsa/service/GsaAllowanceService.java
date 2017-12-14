package gov.nysenate.ess.travel.allowance.gsa.service;

import gov.nysenate.ess.travel.allowance.gsa.model.GsaResponse;
import gov.nysenate.ess.travel.allowance.gsa.model.LodgingAllowance;
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
    private MealIncidentalRatesService mealRatesService;

    @Autowired
    public GsaAllowanceService(GsaClient client, MealIncidentalRatesService mealIncidentalRatesService) {
        this.client = client;
        this.mealRatesService = mealIncidentalRatesService;
    }

    public LodgingAllowance calculateLodging(Itinerary itinerary) throws IOException {
        BigDecimal lodging = BigDecimal.ZERO;
        for (TravelDestination destination: itinerary.getDestinations()) {
            lodging.add(lodgingForDestination(destination));
        }
        return new LodgingAllowance(lodging);
    }

    private BigDecimal lodgingForDestination(TravelDestination destination) throws IOException {
        BigDecimal lodging = BigDecimal.ZERO;
        for (LocalDate date: destination.getNightsOfStay()) {
            GsaResponse res = client.queryGsa(date, destination.getAddress().getZip5());
            lodging.add(res.getLodging(date.getMonth()));
        }
        return lodging;
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
            String mealRow = res.getMealRow();
            // TODO: localMealDao.getBreakfast(zip, mealRow);
            // TODO: localMealDao.getDinner(zip, mealRow);
            // meals.add(breakfast, dinner)
        }
        return meals;
    }
}
