package gov.nysenate.ess.travel.allowance.gsa.service;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.allowance.gsa.model.GsaAllowance;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class GsaAllowanceService {

    @Autowired GsaClient client;

    public GsaAllowance computeAllowance(Itinerary itinerary) {
        int mealAllowance = 0;
        int lodgingAllowance = 0;
        int incidentalAllowance = 0;

        List<TravelDestination> travelDestinations = itinerary.getDestinations();
        for (TravelDestination travelDestination : travelDestinations) {
            if (travelDestination.isWaypoint()) {
                continue;
            }
            LocalDate departure = travelDestination.getDepartureDate();
            LocalDate arrival = travelDestination.getArrivalDate();
            Address destinationAddress = travelDestination.getAddress();

            LocalDate currentDate = travelDestination.getArrivalDate();

            client.setLodging(currentDate.getMonth());

            int daysThere = (int) ChronoUnit.DAYS.between(arrival, departure) + 1;

            // Same day
            if (daysThere == 1) {
                mealAllowance += client.getBreakfastCost();
                mealAllowance += client.getDinnerCost();
                incidentalAllowance += client.getIncidentalCost();
            }
            // More than one day
            else {
                // Handles every but the last day
                for (int i = 1; i < daysThere; i++) {
                    mealAllowance += client.getBreakfastCost();
                    mealAllowance += client.getDinnerCost();

                    lodgingAllowance += client.getLodging();
                    incidentalAllowance += client.getIncidentalCost();

                    currentDate = currentDate.plusDays(1);
                    client = handleIfNewMonth(arrival, currentDate, client, destinationAddress);
                }

                mealAllowance += client.getBreakfastCost();
                mealAllowance += client.getDinnerCost();
            }
        }

        return new GsaAllowance(String.valueOf(mealAllowance), String.valueOf(lodgingAllowance), String.valueOf(incidentalAllowance));
    }

    private GsaClient handleIfNewMonth(LocalDate arrival, LocalDate currentDate, GsaClient client, Address destinationAddress) {
        if (currentDate.getMonth() != arrival.getMonth()) {
            client.scrapeGsa(getFiscalYear(currentDate), destinationAddress.getZip5());
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
