package gov.nysenate.ess.supply.requisition.service;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;

import java.time.LocalDateTime;
import java.util.EnumSet;

public interface RequisitionService {

    void submitNewRequisition(RequisitionVersion version);

    void updateRequisition(int requisitionId, RequisitionVersion newVersion);

    void undoRejection(int requisitionId);

    Requisition getRequisitionById(int requisitionId);

    PaginatedList<Requisition> searchRequisitions(String destination, String customerId, EnumSet<RequisitionStatus> statuses,
                                                  Range<LocalDateTime> dateRange, String dateField, LimitOffset limitOffset);
}
