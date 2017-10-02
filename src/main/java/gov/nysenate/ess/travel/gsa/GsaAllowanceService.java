package gov.nysenate.ess.travel.gsa;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.model.GsaAllowance;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.MealIncidentalRates;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import gov.nysenate.ess.travel.request.model.GsaClient;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class GsaAllowanceService {

    private MealIncidentalRates mealIncidentalRates;

    public GsaAllowance computeAllowance(Itinerary itinerary) {
        int mealAllowance = 0;
        int lodgingAllowance = 0;
        int incidentalAllowance = 0;

        Address address = itinerary.getOrigin();

        List<TravelDestination> travelDestinations = itinerary.getTravelDestinations();
        for (TravelDestination travelDestination : travelDestinations) {
            LocalDateTime departure = travelDestination.getDepartureDateTime();
            LocalDateTime arrival = travelDestination.getArrivalDateTime();
            Address destinationAddress = travelDestination.getAddress();

            LocalDateTime currentDate = travelDestination.getArrivalDateTime();

            GsaClient client = new GsaClient(getFiscalYear(currentDate), destinationAddress.getZip5());
            client.setLodging(currentDate.getMonth());

            mealIncidentalRates = client.getMealIncidentalRates();

            int daysThere = (int) ChronoUnit.DAYS.between(arrival, departure) + 1;
            if (arrival.getHour() > departure.getHour()) {
                daysThere++;
            }

            // Same day
            if (daysThere == 1) {
                // Arriving before 7am: breakfast
                if (arrival.getHour() < 7) {
                    mealAllowance += mealIncidentalRates.getBreakfastCost();
                }

                // Leaving after 7pm: dinner
                if (departure.getHour() >= 19) {
                    mealAllowance += mealIncidentalRates.getDinnerCost();
                }
            }
            // More than one day
            else {
                // Handles the first day
                if (arrival.getHour() < 7) {
                    // Arriving before 7am: breakfast
                    mealAllowance += mealIncidentalRates.getBreakfastCost();
                }
                if (arrival.getHour() < 19) {
                    // Get dinner as you are staying the night
                    mealAllowance += mealIncidentalRates.getDinnerCost();
                }

                lodgingAllowance += client.getLodging();
                currentDate = currentDate.plusDays(1);
                client = handleIfNewMonth(arrival, currentDate, client, destinationAddress);

                // Handles the inner days (not first or last)
                for (int i = 2; i < daysThere; i++) {
                    mealAllowance += mealIncidentalRates.getBreakfastCost();
                    mealAllowance += mealIncidentalRates.getDinnerCost();

                    lodgingAllowance += client.getLodging();
                    currentDate = currentDate.plusDays(1);
                    client = handleIfNewMonth(arrival, currentDate, client, destinationAddress);
                }

                // Handles the last day
                if (departure.getHour() > 7) {
                    // Leaving after 7am: breakfast
                    mealAllowance += mealIncidentalRates.getBreakfastCost();
                }
                if (departure.getHour() >= 19) {
                    // Leaving after 7pm: dinner
                    mealAllowance += mealIncidentalRates.getDinnerCost();
                }
            }
        }

        return new GsaAllowance(String.valueOf(mealAllowance), String.valueOf(lodgingAllowance), String.valueOf(incidentalAllowance));
    }

    private GsaClient handleIfNewMonth(LocalDateTime arrival, LocalDateTime currentDate, GsaClient client, Address destinationAddress) {
        GsaClient newClient = client;

        if (currentDate.getMonth() != arrival.getMonth()) {
            // New month
            newClient = new GsaClient(getFiscalYear(currentDate), destinationAddress.getZip5());
            newClient.setLodging(currentDate.getMonth());
            mealIncidentalRates = newClient.getMealIncidentalRates();
        }

        return newClient;
    }

    private int getFiscalYear(LocalDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();

        int fiscalYear = year;
        if (month >= Month.OCTOBER.getValue()) {
            fiscalYear++;
        }

        return fiscalYear;
    }
}
