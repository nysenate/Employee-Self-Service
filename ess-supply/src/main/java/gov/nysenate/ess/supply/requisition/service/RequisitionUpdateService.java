package gov.nysenate.ess.supply.requisition.service;

import gov.nysenate.ess.supply.requisition.RequisitionVersion;

import java.time.LocalDateTime;

public interface RequisitionUpdateService {

    void submitNewRequisition(RequisitionVersion version);

    void updateRequisition(int requisitionId, RequisitionVersion newVersion);

    void undoRejection(int requisitionId);
}
