package gov.nysenate.ess.travel.application.route.destination;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_DATE;

public class DestinationView implements ViewObject {

    String id;
    AddressView address;
    String arrival;
    String departure;
    Map<String, String> mealPerDiems;
    Map<String, String> lodgingPerDiems;

    public DestinationView() {
    }

    public DestinationView(Destination destination) {
        this.id = String.valueOf(destination.getId());
        this.address = new AddressView(destination.getAddress());
        this.arrival = destination.getDateRange() == null ? null : destination.arrivalDate().format(ISO_DATE);
        this.departure = destination.getDateRange() == null ? null : destination.departureDate().format(ISO_DATE);
        this.mealPerDiems = destination.getMealPerDiems().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().format(ISO_DATE),
                        entry -> entry.getValue().toString()
                ));
        this.lodgingPerDiems = destination.getLodgingPerDiems().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().format(ISO_DATE),
                        entry -> entry.getValue().toString()
                ));
    }

    public Destination toDestination() {
        TreeMap<LocalDate, Dollars> mealPerDiems = new TreeMap<>();
        TreeMap<LocalDate, Dollars> lodgingPerDiems = new TreeMap<>();

        if (this.mealPerDiems != null) {
            for (Map.Entry<String, String> entry : this.mealPerDiems.entrySet()) {
                mealPerDiems.put(LocalDate.parse(entry.getKey(), ISO_DATE), new Dollars(entry.getValue()));
            }
        }


        if (this.lodgingPerDiems != null) {
            for (Map.Entry<String, String> entry : this.lodgingPerDiems.entrySet()) {
                lodgingPerDiems.put(LocalDate.parse(entry.getKey(), ISO_DATE), new Dollars(entry.getValue()));
            }
        }

        int destId = id == null || id.isEmpty() ? 0 : Integer.valueOf(id);
        // FIXME Defaulting to now() sucks.
        LocalDate arrival = this.arrival == null ? null : LocalDate.parse(this.arrival, ISO_DATE);
        LocalDate departure = this.departure == null ? null : LocalDate.parse(this.departure, ISO_DATE);
        return new Destination(destId, address.toAddress(), arrival,
                departure, mealPerDiems, lodgingPerDiems);
    }

    public String getId() {
        return id;
    }

    public AddressView getAddress() {
        return address;
    }

    public String getArrival() {
        return arrival;
    }

    public String getDeparture() {
        return departure;
    }

    public Map<String, String> getMealPerDiems() {
        return mealPerDiems;
    }

    public Map<String, String> getLodgingPerDiems() {
        return lodgingPerDiems;
    }

    @Override
    public String getViewType() {
        return "destination";
    }
}
