package gov.nysenate.ess.supply.requisition.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;

import java.time.LocalDateTime;
import java.util.EnumSet;

public interface RequisitionDao {

    int saveRequisition(Requisition requisition);

    Requisition getRequisitionById(int requisitionId);

    PaginatedList<Requisition> searchRequisitions(String destination, String customerId, EnumSet<RequisitionStatus> statuses,
                                                  Range<LocalDateTime> dateRange, String dateField, LimitOffset limitOffset);

    PaginatedList<Requisition> getOrderHistory(String destination, String customerId, EnumSet<RequisitionStatus> statuses,
                                               Range<LocalDateTime> dateRange, String dateField, LimitOffset limitOffset);
}
