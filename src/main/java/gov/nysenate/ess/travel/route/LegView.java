package gov.nysenate.ess.travel.route;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LegView implements ViewObject {

    private AddressView from;
    private AddressView to;
    private String miles;
    private String modeOfTransportation;
    @JsonProperty(value="isMileageRequested")
    private boolean isMileageRequested;
    private String travelDate;
    @JsonProperty(value="qualifies")
    private boolean qualifies; // Does this leg qualify for reimbursement.

    public LegView() {
    }

    public LegView(Leg leg) {
        this.from = new AddressView(leg.getFrom());
        this.to = new AddressView(leg.getTo());
        this.miles = String.valueOf(leg.getMiles());
        this.modeOfTransportation = leg.getModeOfTransportation().getDisplayName();
        this.isMileageRequested = leg.isMileageRequested();
        this.travelDate = leg.getTravelDate().format(DateTimeFormatter.ISO_DATE);
        this.qualifies = leg.qualifies();
    }

    public Leg toLeg() {
        return new Leg(from.toAddress(), to.toAddress(), Double.valueOf(miles),
                ModeOfTransportation.of(modeOfTransportation),
                LocalDate.parse(travelDate, DateTimeFormatter.ISO_DATE), isMileageRequested);
    }

    public AddressView getFrom() {
        return from;
    }

    public AddressView getTo() {
        return to;
    }

    public String getMiles() {
        return miles;
    }

    public String getModeOfTransportation() {
        return modeOfTransportation;
    }

    public boolean isMileageRequested() {
        return isMileageRequested;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public boolean isQualifies() {
        return qualifies;
    }

    @Override
    public String getViewType() {
        return "route leg";
    }
}
