package gov.nysenate.ess.travel.route;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LegView implements ViewObject {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private AddressView from;
    private AddressView to;
    private String miles;
    private ModeOfTransportationView modeOfTransportation;
    private String travelDate;
    @JsonProperty(value="qualifies")
    private boolean qualifies; // Does this leg qualify for reimbursement.

    public LegView() {
    }

    public LegView(Leg leg) {
        this.from = new AddressView(leg.getFrom());
        this.to = new AddressView(leg.getTo());
        this.miles = String.valueOf(leg.getMiles());
        this.modeOfTransportation = new ModeOfTransportationView(leg.getModeOfTransportation());
        this.travelDate = leg.getTravelDate().format(DATE_FORMAT);
        this.qualifies = leg.qualifies();
    }

    public Leg toLeg() {
        return new Leg(from.toAddress(), to.toAddress(),
                miles == null ? new Double("0") : Double.valueOf(miles),
                modeOfTransportation.toModeOfTransportation(),
                LocalDate.parse(travelDate, DATE_FORMAT));
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

    public ModeOfTransportationView getModeOfTransportation() {
        return modeOfTransportation;
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
