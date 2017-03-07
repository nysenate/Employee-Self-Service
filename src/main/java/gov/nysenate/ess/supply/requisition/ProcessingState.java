package gov.nysenate.ess.supply.requisition;

public class ProcessingState implements RequisitionState {
    @Override
    public Requisition process(Requisition requisition) {
        return null;
    }

    @Override
    public Requisition reject(Requisition requisition) {
        return null;
    }
}
