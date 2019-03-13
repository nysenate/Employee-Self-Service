package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.route.destination.DestinationView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LegView implements ViewObject {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private String id;
    private DestinationView from;
    private DestinationView to;
    private ModeOfTransportationView modeOfTransportation;
    private String travelDate;
    private String miles;
    private String mileageRate;
    private String mileageExpense;

    public LegView() {
    }

    public LegView(Leg leg) {
        this.id = String.valueOf(leg.getId());
        this.from = new DestinationView(leg.getFrom());
        this.to = new DestinationView(leg.getTo());
        this.modeOfTransportation = new ModeOfTransportationView(leg.getModeOfTransportation());
        this.travelDate = leg.getTravelDate().format(DATE_FORMAT);
        this.miles = String.valueOf(leg.getMiles());
        this.mileageRate = leg.getMileageRate().toString();
        this.mileageExpense = leg.mileageExpense().toString();
    }

    public Leg toLeg() {
        int legId = id == null || id.isEmpty() ? 0 : Integer.valueOf(id);
        return new Leg(
                legId,
                from.toDestination(),
                to.toDestination(),
                modeOfTransportation.toModeOfTransportation(),
                LocalDate.parse(travelDate, DATE_FORMAT),
                miles == null ? 0 : Double.valueOf(miles),
                mileageRate == null ? new BigDecimal("0") : new BigDecimal(mileageRate));
    }

    public String getId() {
        return id;
    }

    public DestinationView getFrom() {
        return from;
    }

    public DestinationView getTo() {
        return to;
    }

    public String getMiles() {
        return miles;
    }

    public String getMileageRate() {
        return mileageRate;
    }

    public ModeOfTransportationView getModeOfTransportation() {
        return modeOfTransportation;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public String getMileageExpense() {
        return mileageExpense;
    }

    @Override
    public String getViewType() {
        return "route leg";
    }
}
