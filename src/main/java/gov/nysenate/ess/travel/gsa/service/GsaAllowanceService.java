package gov.nysenate.ess.travel.gsa.service;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.model.GsaAllowance;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.MealIncidentalRates;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import gov.nysenate.ess.travel.request.model.GsaClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class GsaAllowanceService {

    public GsaAllowance computeAllowance(Itinerary itinerary) {
        int mealAllowance = 0;
        int lodgingAllowance = 0;

        List<TravelDestination> travelDestinations = itinerary.getTravelDestinations();
        for (TravelDestination travelDestination : travelDestinations) {
            LocalDate departure = travelDestination.getDepartureDate();
            LocalDate arrival = travelDestination.getArrivalDate();
            Address destinationAddress = travelDestination.getAddress();

            LocalDate currentDate = travelDestination.getArrivalDate();

            GsaClient client = new GsaClient(getFiscalYear(currentDate), destinationAddress.getZip5());
            client.setLodging(currentDate.getMonth());
            MealIncidentalRates mealIncidentalRates = client.getMealIncidentalRates();

            int daysThere = (int) ChronoUnit.DAYS.between(arrival, departure) + 1;

            // Same day
            if (daysThere == 1) {
                mealAllowance += mealIncidentalRates.getBreakfastCost();
                mealAllowance += mealIncidentalRates.getDinnerCost();
            }
            // More than one day
            else {
                // Handles every but the last day
                for (int i = 1; i < daysThere; i++) {
                    mealAllowance += mealIncidentalRates.getBreakfastCost();
                    mealAllowance += mealIncidentalRates.getDinnerCost();

                    lodgingAllowance += client.getLodging();
                    currentDate = currentDate.plusDays(1);
                    client = handleIfNewMonth(arrival, currentDate, client, destinationAddress);
                    mealIncidentalRates = client.getMealIncidentalRates();
                }

                mealAllowance += mealIncidentalRates.getBreakfastCost();
                mealAllowance += mealIncidentalRates.getDinnerCost();
            }
        }

        return new GsaAllowance(String.valueOf(mealAllowance), String.valueOf(lodgingAllowance), "0");
    }

    private GsaClient handleIfNewMonth(LocalDate arrival, LocalDate currentDate, GsaClient client, Address destinationAddress) {
        if (currentDate.getMonth() != arrival.getMonth()) {
            client = new GsaClient(getFiscalYear(currentDate), destinationAddress.getZip5());
            client.setLodging(currentDate.getMonth());
        }

        return client;
    }

    private int getFiscalYear(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();

        int fiscalYear = year;
        if (month >= Month.OCTOBER.getValue()) {
            fiscalYear++;
        }

        return fiscalYear;
    }
}
