package gov.nysenate.ess.supply.requisition.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public interface RequisitionDao {

    Requisition saveRequisition(Requisition requisition);

    Optional<Requisition> getRequisitionById(int requisitionId);

    PaginatedList<Requisition> searchRequisitions(String destination, String customerId, EnumSet<RequisitionStatus> statuses,
                                                  Range<LocalDateTime> dateRange, String dateField, String savedInSfms, LimitOffset limitOffset);

    PaginatedList<Requisition> searchRequisitions(String destination, String customerId, EnumSet<RequisitionStatus> statuses,
                                                  Range<LocalDateTime> dateRange, String dateField, String savedInSfms, LimitOffset limitOffset, String issuerId);

    /**
     * Searches the order history for a employee.
     * An employees order history includes all orders for his work location plus all orders they have made themselves
     * to any other locations.
     * This is similar to the searchRequisitions method except it requires a destinationId and customerId and
     * should only be used to get an employees order history.
     */
    PaginatedList<Requisition> searchOrderHistory(String destinationId, int customerId, EnumSet<RequisitionStatus> statuses,
                                                  Range<LocalDateTime> dateRange, String dateField, LimitOffset limitOffset);

    ImmutableList<Requisition> getRequisitionHistory(int requisitionId);

    /**
     * Marks requisitions as saved in SFMS.
     * @param requisitionIds The id's of the requisitions to be marked as saved.
     */
    void savedInSfms(List<Integer> requisitionIds);
}
