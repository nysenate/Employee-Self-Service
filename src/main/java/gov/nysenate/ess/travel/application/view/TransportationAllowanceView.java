package gov.nysenate.ess.travel.application.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.model.TransportationAllowance;

public class TransportationAllowanceView implements ViewObject {

    private String mileage;
    private String tolls;
    private String total;

    public TransportationAllowanceView(TransportationAllowance ta) {
        this.mileage = ta.getMileage().toString();
        this.tolls = ta.getTolls().toString();
        this.total = ta.total().toString();
    }

    public TransportationAllowance toTransportationAllowance() {
        return new TransportationAllowance(mileage, tolls);
    }

    public String getMileage() {
        return mileage;
    }

    public String getTolls() {
        return tolls;
    }

    public String getTotal() {
        return total;
    }

    @Override
    public String getViewType() {
        return "transportation-allowance";
    }
}
