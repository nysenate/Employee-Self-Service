package gov.nysenate.ess.supply.requisition;

public interface RequisitionState {

    Requisition process(Requisition requisition);

    Requisition reject(Requisition requisition);
}
