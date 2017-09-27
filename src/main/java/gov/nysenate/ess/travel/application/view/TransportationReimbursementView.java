package gov.nysenate.ess.travel.application.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.model.TransportationReimbursement;

public class TransportationReimbursementView implements ViewObject {

    private String milage;
    private String tolls;
    private String total;

    public TransportationReimbursementView(TransportationReimbursement tr) {
        this.milage = tr.getMilage().toString();
        this.tolls = tr.getTolls().toString();
        this.total = tr.total().toString();
    }

    public TransportationReimbursement toTransportReimbursement() {
        return new TransportationReimbursement(milage, tolls);
    }

    public String getMilage() {
        return milage;
    }

    public String getTolls() {
        return tolls;
    }

    public String getTotal() {
        return total;
    }

    @Override
    public String getViewType() {
        return "transportation-reimbursement";
    }
}
