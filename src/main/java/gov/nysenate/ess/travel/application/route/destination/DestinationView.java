package gov.nysenate.ess.travel.application.route.destination;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.address.GoogleAddressView;
import gov.nysenate.ess.travel.application.allowances.PerDiemView;
import gov.nysenate.ess.travel.provider.gsa.meal.GsaMie;
import gov.nysenate.ess.travel.provider.gsa.meal.GsaMieView;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_DATE;

public class DestinationView implements ViewObject {

    private int id;
    private GoogleAddressView address;
    private String arrivalDate;
    private String departureDate;
    private Map<String, GsaMieView> mealPerDiems;
    private List<PerDiemView> lodgingPerDiems;

    public DestinationView() {
    }

    public DestinationView(Destination d) {
        this.id = d.id;
        this.address = new GoogleAddressView(d.getAddress());
        this.arrivalDate = d.arrivalDate() == null ? null : d.arrivalDate().format(ISO_DATE);
        this.departureDate = d.departureDate() == null ? null : d.departureDate().format(ISO_DATE);
        mealPerDiems = new HashMap<>();
        for (Map.Entry<LocalDate, GsaMie> entry : d.mealPerDiems().entrySet()) {
            mealPerDiems.put(entry.getKey().format(ISO_DATE), new GsaMieView(entry.getValue()));
        }
        this.lodgingPerDiems = d.lodgingPerDiems().stream().map(PerDiemView::new).collect(Collectors.toList());
    }

    public Destination toDestination() {
        Map<LocalDate, GsaMie> dateToGsaMie = new HashMap<>();
        if (mealPerDiems != null) {
            for (Map.Entry<String, GsaMieView> entry : mealPerDiems.entrySet()) {
                dateToGsaMie.put(LocalDate.parse(entry.getKey(), ISO_DATE), entry.getValue().toGsaMie());
            }
        }
        return new Destination(
                id,
                address.toGoogleAddress(),
                arrivalDate == null ? null : LocalDate.parse(arrivalDate, ISO_DATE),
                departureDate == null ? null : LocalDate.parse(departureDate, ISO_DATE),
                dateToGsaMie,
                lodgingPerDiems == null ? null : lodgingPerDiems.stream().map(PerDiemView::toPerDiem).collect(Collectors.toList())
        );
    }

    public int getId() {
        return id;
    }

    public GoogleAddressView getAddress() {
        return address;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public Map<String, GsaMieView> getMealPerDiems() {
        return mealPerDiems;
    }

    public List<PerDiemView> getLodgingPerDiems() {
        return lodgingPerDiems;
    }

    @Override
    public String getViewType() {
        return "destination";
    }
}
