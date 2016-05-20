package gov.nysenate.ess.supply.requisition.service;

import gov.nysenate.ess.supply.requisition.RequisitionVersion;

import java.time.LocalDateTime;

public interface RequisitionUpdateService {

    void submitNewRequisition(LocalDateTime orderedDateTime, RequisitionVersion version);

}
