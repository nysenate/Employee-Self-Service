package gov.nysenate.ess.travel.application.route;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.unit.Address;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LegView implements ViewObject {

    private String requestedPerDiem;
    private String maximumPerDiem;
    private AddressView from;
    private AddressView to;
    private String date;
    private String miles;
    private String mileageRate;
    private String methodOfTravelDisplayName;
    private String methodOfTravelDescription;
    @JsonProperty("isReimbursementRequested")
    private boolean isReimbursementRequested;
    @JsonProperty("isOutbound")
    private boolean isOutbound;

    public LegView() {
    }

    public LegView(Leg leg) {
        this.requestedPerDiem = leg.requestedPerDiem().toString();
        this.maximumPerDiem = leg.maximumPerDiem().toString();
        this.from = new AddressView(leg.fromAddress());
        this.to = new AddressView(leg.toAddress());
        this.date = leg.travelDate().format(DateTimeFormatter.ISO_DATE);
        this.miles = new BigDecimal(leg.miles()).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
        this.mileageRate = leg.mileageRate().toString();
        this.methodOfTravelDisplayName = leg.methodOfTravelDisplayName();
        this.methodOfTravelDescription = leg.methodOfTravelDescription();
        this.isReimbursementRequested = leg.isReimbursementRequested();
        this.isOutbound = leg.isOutbound();
    }

    @JsonIgnore
    public LocalDate date() {
        return LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
    }

    @JsonIgnore
    public Address fromAddress() {
        return from.toAddress();
    }

    @JsonIgnore
    public Address toAddress() {
        return to.toAddress();
    }

    @JsonIgnore
    public BigDecimal rate() {
        return new BigDecimal(mileageRate);
    }

    public String getRequestedPerDiem() {
        return requestedPerDiem;
    }

    public String getMaximumPerDiem() {
        return maximumPerDiem;
    }

    public AddressView getFrom() {
        return from;
    }

    public AddressView getTo() {
        return to;
    }

    public String getDate() {
        return date;
    }

    public String getMiles() {
        return miles;
    }

    public String getMileageRate() {
        return mileageRate;
    }

    public String getMethodOfTravelDisplayName() {
        return methodOfTravelDisplayName;
    }

    public String getMethodOfTravelDescription() {
        return methodOfTravelDescription;
    }

    public boolean isReimbursementRequested() {
        return isReimbursementRequested;
    }

    public boolean isOutbound() {
        return isOutbound;
    }

    @Override
    public String getViewType() {
        return "leg";
    }
}
