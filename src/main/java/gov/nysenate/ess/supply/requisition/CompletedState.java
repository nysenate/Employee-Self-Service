package gov.nysenate.ess.supply.requisition;

public class CompletedState implements RequisitionState {
    @Override
    public Requisition process(Requisition requisition) {
        return null;
    }

    @Override
    public Requisition reject(Requisition requisition) {
        return null;
    }
}
