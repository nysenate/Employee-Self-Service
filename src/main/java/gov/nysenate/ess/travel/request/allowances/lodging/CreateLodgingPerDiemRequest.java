package gov.nysenate.ess.travel.request.allowances.lodging;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.request.address.TravelAddressView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ISO_DATE;

public class CreateLodgingPerDiemRequest implements ViewObject {

    private static final DateTimeFormatter DATE_FORMAT = ISO_DATE;

    private String date;
    private TravelAddressView address;

    public CreateLodgingPerDiemRequest() {
    }

    public LocalDate date() {
        return LocalDate.parse(date, DATE_FORMAT);
    }

    public TravelAddress travelAddress() {
        return address.toTravelAddress();
    }

    public String getDate() {
        return date;
    }

    public TravelAddressView getAddress() {
        return address;
    }

    @Override
    public String getViewType() {
        return "Create-Lodging-Per-Diem-Request";
    }
}
