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

        List<TravelDestination> travelDestinations = itinerary.getDestinations();
        for (TravelDestination travelDestination : travelDestinations) {
            if (travelDestination.isWaypoint()) {
                continue;
            }
            LocalDate departure = travelDestination.getDepartureDate();
            LocalDate arrival = travelDestination.getArrivalDate();
            Address destinationAddress = travelDestination.getAddress();

            LocalDate currentDate = travelDestination.getArrivalDate();

            client.scrapeGsa(getFiscalYear(currentDate), destinationAddress.getZip5());
            client.setLodging(currentDate.getMonth());

            int daysThere = (int) ChronoUnit.DAYS.between(arrival, departure) + 1;

            for (int i = 1; i < daysThere; i++) {
                mealAllowance += client.getBreakfastCost();
                mealAllowance += client.getDinnerCost();

                lodgingAllowance += client.getLodging();

                currentDate = currentDate.plusDays(1);
                handleIfNewMonth(arrival, currentDate, destinationAddress);
            }

            mealAllowance += client.getBreakfastCost();
            mealAllowance += client.getDinnerCost();
        }

        return new GsaAllowance(String.valueOf(mealAllowance), String.valueOf(lodgingAllowance));
    }

    private void handleIfNewMonth(LocalDate arrival, LocalDate currentDate, Address destinationAddress) {
        if (currentDate.getMonth() != arrival.getMonth()) {
            client.scrapeGsa(getFiscalYear(currentDate), destinationAddress.getZip5());
            client.setLodging(currentDate.getMonth());
        }
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
