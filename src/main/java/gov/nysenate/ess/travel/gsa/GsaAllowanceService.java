package gov.nysenate.ess.travel.gsa;

import com.google.gson.JsonObject;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.model.GsaAllowance;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import gov.nysenate.ess.travel.request.model.GsaClient;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class GsaAllowanceService {

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

            int daysThere = (int) departure.until(arrival, DAYS) + 1;
            if (daysThere == 1) {
                if (arrival.getHour() < 7) {

                }
            }

            GsaClient client = new GsaClient(2017, destinationAddress.getZip5());
            client.setLodging(arrival.getMonth());
            JsonObject object = client.getRecords();
        }

        GsaAllowance gsaAllowance = new GsaAllowance(String.valueOf(mealAllowance), String.valueOf(lodgingAllowance), String.valueOf(incidentalAllowance));
        System.out.println(gsaAllowance.getIncidental());

        return null;
    }

    private int getFiscalYear(LocalDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();

        int fiscalYear = year;
        if (month >= Month.OCTOBER.getValue()) {
            fiscalYear++;
        }
        else {
            fiscalYear--;
        }

        return fiscalYear;
    }
}
