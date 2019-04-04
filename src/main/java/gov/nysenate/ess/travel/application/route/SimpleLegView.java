package gov.nysenate.ess.travel.application.route;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SimpleLegView implements ViewObject {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private String id;
    private AddressView from;
    private AddressView to;
    private String methodOfTravel;
    private String methodOfTravelDescription;
    private String travelDate;

    public SimpleLegView() {
    }

    public SimpleLegView(Leg leg) {
        this.id = String.valueOf(leg.getId());
        this.from = new AddressView(leg.fromAddress());
        this.to = new AddressView(leg.toAddress());
        this.methodOfTravel = leg.methodOfTravel();
        this.methodOfTravelDescription = leg.methodOfTravelDescription();
        this.travelDate = leg.travelDate().format(DATE_FORMAT);
    }

    @JsonIgnore
    public LocalDate travelDate() {
        return LocalDate.parse(getTravelDate(), DATE_FORMAT);
    }

    @JsonIgnore
    public ModeOfTransportation modeOfTransportation() {
        return new ModeOfTransportation(methodOfTravel, methodOfTravelDescription);
    }

    public String getId() {
        return id;
    }

    public AddressView getFrom() {
        return from;
    }

    public AddressView getTo() {
        return to;
    }

    public String getMethodOfTravel() {
        return methodOfTravel;
    }

    public String getMethodOfTravelDescription() {
        return methodOfTravelDescription;
    }

    public String getTravelDate() {
        return travelDate;
    }

    @Override
    public String getViewType() {
        return "simple-leg";
    }
}
