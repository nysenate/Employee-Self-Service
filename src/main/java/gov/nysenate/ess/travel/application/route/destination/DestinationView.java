package gov.nysenate.ess.travel.application.route.destination;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.address.GoogleAddressView;
import gov.nysenate.ess.travel.application.allowances.PerDiemView;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_DATE;

public class DestinationView implements ViewObject {

    private int id;
    private GoogleAddressView address;
    private String arrivalDate;
    private String departureDate;
    private List<PerDiemView> mealPerDiems;
    private List<PerDiemView> lodgingPerDiems;

    public DestinationView() {
    }

    public DestinationView(Destination d) {
        this.id = d.id;
        this.address = new GoogleAddressView(d.getAddress());
        this.arrivalDate = d.arrivalDate() == null ? null : d.arrivalDate().format(ISO_DATE);
        this.departureDate = d.departureDate() == null ? null : d.departureDate().format(ISO_DATE);
        this.mealPerDiems = d.mealPerDiems().stream().map(PerDiemView::new).collect(Collectors.toList());
        this.lodgingPerDiems = d.lodgingPerDiems().stream().map(PerDiemView::new).collect(Collectors.toList());
    }

    public Destination toDestination() {
        return new Destination(
                id,
                address.toGoogleAddress(),
                arrivalDate == null ? null : LocalDate.parse(arrivalDate, ISO_DATE),
                departureDate == null ? null : LocalDate.parse(departureDate, ISO_DATE),
                mealPerDiems == null ? null : mealPerDiems.stream().map(PerDiemView::toPerDiem).collect(Collectors.toList()),
                lodgingPerDiems == null ? null : lodgingPerDiems.stream().map(PerDiemView::toPerDiem).collect(Collectors.toList())
        );
    }

    public int getId() {
        return id;
    }

    public AddressView getAddress() {
        return address;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    @Override
    public String getViewType() {
        return "destination";
    }
}
