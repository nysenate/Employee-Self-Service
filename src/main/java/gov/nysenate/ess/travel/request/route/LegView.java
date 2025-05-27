package gov.nysenate.ess.travel.request.route;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.request.allowances.PerDiem;
import gov.nysenate.ess.travel.request.route.destination.DestinationView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LegView implements ViewObject {

    private static final DateTimeFormatter DATEPICKER_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private int id;
    private DestinationView from;
    private DestinationView to;
    private String methodOfTravelDisplayName;
    private String methodOfTravelDescription;
    private String travelDate;
    @JsonProperty("isOutbound")
    private boolean isOutbound;

    public LegView() {
    }

    public LegView(Leg leg) {
        this.id = leg.getId();
        this.from = new DestinationView(leg.from());
        this.to = new DestinationView(leg.to());
        this.methodOfTravelDisplayName = leg.methodOfTravelDisplayName();
        this.methodOfTravelDescription = leg.methodOfTravelDescription();
        this.travelDate = leg.travelDate().format(DATEPICKER_FORMAT);
        this.isOutbound = leg.isOutbound();
    }

    public Leg toLeg() {
        return new Leg(
                id,
                from.toDestination(),
                to.toDestination(),
                new ModeOfTransportation(this.methodOfTravelDisplayName, this.methodOfTravelDescription),
                isOutbound,
                LocalDate.parse(travelDate, DATEPICKER_FORMAT)
        );
    }

    public int getId() {
        return id;
    }

    public DestinationView getFrom() {
        return from;
    }

    public DestinationView getTo() {
        return to;
    }

    public String getMethodOfTravelDisplayName() {
        return methodOfTravelDisplayName;
    }

    public String getMethodOfTravelDescription() {
        return methodOfTravelDescription;
    }

    public String getTravelDate() {
        return travelDate;
    }

    @JsonIgnore
    public boolean isOutbound() {
        return isOutbound;
    }

    @Override
    public String getViewType() {
        return "leg";
    }
}
