package gov.nysenate.ess.travel.application.route;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.application.route.destination.DestinationView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LegView implements ViewObject {

    private static final DateTimeFormatter DATEPICKER_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private int id;
    private DestinationView from;
    private DestinationView to;
    private String methodOfTravelDisplayName;
    private String methodOfTravelDescription;
    private String miles;
    private String travelDate;
    private String mileageRate;
    @JsonProperty("isReimbursementRequested")
    private boolean isReimbursementRequested;
    @JsonProperty("isOutbound")
    private boolean isOutbound;
    private String requestedPerDiem;
    private String maximumPerDiem;

    public LegView() {
    }

    public LegView(Leg leg) {
        this.id = leg.getId();
        this.from = new DestinationView(leg.from());
        this.to = new DestinationView(leg.to());
        this.methodOfTravelDisplayName = leg.methodOfTravelDisplayName();
        this.methodOfTravelDescription = leg.methodOfTravelDescription();
        this.miles = new BigDecimal(leg.miles()).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
        this.travelDate = leg.travelDate().format(DATEPICKER_FORMAT);
        this.mileageRate = leg.mileageRate().toString();
        this.isReimbursementRequested = leg.isReimbursementRequested();
        this.isOutbound = leg.isOutbound();
        this.requestedPerDiem = leg.requestedPerDiem().toString();
        this.maximumPerDiem = leg.maximumPerDiem().toString();
    }

    public Leg toLeg() {
        return new Leg(
                id,
                from.toDestination(),
                to.toDestination(),
                new ModeOfTransportation(this.methodOfTravelDisplayName, this.methodOfTravelDescription),
                miles == null ? Double.valueOf(0) : Double.valueOf(miles),
                createPerDiem(),
                isOutbound
        );
    }

    private PerDiem createPerDiem() {
        return new PerDiem(
                LocalDate.parse(travelDate, DATEPICKER_FORMAT),
                mileageRate == null ? new BigDecimal(0) : new BigDecimal(mileageRate),
                isReimbursementRequested
        );
    }

    @JsonIgnore
    public LocalDate date() {
        return LocalDate.parse(travelDate, DATEPICKER_FORMAT);
    }

    @JsonIgnore
    public Address fromAddress() {
        return from.toDestination().getAddress();
    }

    @JsonIgnore
    public Address toAddress() {
        return to.toDestination().getAddress();
    }

    @JsonIgnore
    public BigDecimal mileageRate() {
        return new BigDecimal(mileageRate);
    }

    @JsonIgnore
    public ModeOfTransportation modeOfTransportation() {
        return new ModeOfTransportation(methodOfTravelDisplayName, methodOfTravelDescription);
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

    public String getMiles() {
        return miles;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public String getMileageRate() {
        return mileageRate;
    }

    public boolean isReimbursementRequested() {
        return isReimbursementRequested;
    }

    public boolean isOutbound() {
        return isOutbound;
    }

    public String getRequestedPerDiem() {
        return requestedPerDiem;
    }

    public String getMaximumPerDiem() {
        return maximumPerDiem;
    }

    @Override
    public String getViewType() {
        return "leg";
    }
}
