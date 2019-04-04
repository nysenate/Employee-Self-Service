package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class LegView implements ViewObject {

    private String requestedAllowance;
    private String maximumAllowance;
    private AddressView from;
    private AddressView to;
    private String date;
    private String miles;
    private String mileageRate;
    private String methodOfTravel;
    private String methodOfTravelDescription;
    private boolean reimbursementRequested;
    private boolean outbound;

    public LegView() {
    }

    public LegView(Leg leg) {
        this.requestedAllowance = leg.requestedAllowance().toString();
        this.maximumAllowance = leg.maximumAllowance().toString();
        this.from = new AddressView(leg.fromAddress());
        this.to = new AddressView(leg.toAddress());
        this.date = leg.travelDate().format(DateTimeFormatter.ISO_DATE);
        this.miles = new BigDecimal(leg.miles()).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
        this.mileageRate = leg.mileageRate().toString();
        this.methodOfTravel = leg.methodOfTravel();
        this.methodOfTravelDescription = leg.methodOfTravelDescription();
        this.reimbursementRequested = leg.isReimbursementRequested();
        this.outbound = leg.isOutbound();
    }

    public String getRequestedAllowance() {
        return requestedAllowance;
    }

    public String getMaximumAllowance() {
        return maximumAllowance;
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

    public String getMethodOfTravel() {
        return methodOfTravel;
    }

    public String getMethodOfTravelDescription() {
        return methodOfTravelDescription;
    }

    public boolean isReimbursementRequested() {
        return reimbursementRequested;
    }

    public boolean isOutbound() {
        return outbound;
    }

    @Override
    public String getViewType() {
        return "leg";
    }
}
