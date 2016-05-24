package gov.nysenate.ess.supply.requisition.dao;

import gov.nysenate.ess.supply.requisition.Requisition;

public interface RequisitionDao {

    int saveRequisition(Requisition requisition);

    Requisition getRequisitionById(int requisitionId);
}
