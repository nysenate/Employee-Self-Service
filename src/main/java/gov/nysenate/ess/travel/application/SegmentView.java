package gov.nysenate.ess.travel.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.route.ModeOfTransportation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SegmentView implements ViewObject {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private AddressView from;
    private AddressView to;
    private String departureDate;
    private String arrivalDate;
    private String modeOfTransportation;
    @JsonProperty(value="isMileageRequested")
    private boolean isMileageRequested;
    @JsonProperty(value="isMealsRequested")
    private boolean isMealsRequested;
    @JsonProperty(value="isLodgingRequested")
    private boolean isLodgingRequested;

    public SegmentView() {
    }

    public Segment toSegment() {
        Segment s = new Segment();
        s.setFrom(from.toAddress());
        s.setTo(to.toAddress());
        s.setDepartureDate(LocalDate.parse(departureDate, DATE_FORMAT));
        s.setArrivalDate(LocalDate.parse(arrivalDate, DATE_FORMAT));
        s.setModeOfTransportation(ModeOfTransportation.of(modeOfTransportation));
        s.setMileageRequested(isMileageRequested);
        s.setMealsRequested(isMealsRequested);
        s.setLodgingRequested(isLodgingRequested);
        return s;
    }

    public AddressView getFrom() {
        return from;
    }

    public AddressView getTo() {
        return to;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public String getModeOfTransportation() {
        return modeOfTransportation;
    }

    public boolean isMileageRequested() {
        return isMileageRequested;
    }

    public boolean isMealsRequested() {
        return isMealsRequested;
    }

    public boolean isLodgingRequested() {
        return isLodgingRequested;
    }

    @Override
    public String getViewType() {
        return "segment";
    }
}
