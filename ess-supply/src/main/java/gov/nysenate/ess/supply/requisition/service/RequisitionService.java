package gov.nysenate.ess.supply.requisition.service;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;

import java.time.LocalDateTime;
import java.util.EnumSet;

public interface RequisitionService {

    /**
     * Saves a {@link Requisition} without ensuring update consistency.
     * This should only be used for saving a new requisition. For updating a requisition
     * see {@link #updateRequisition(int, Requisition, LocalDateTime) updateRequisition}.
     * @return The requisition id.
     */
    int saveRequisition(Requisition requisition);

    /**
     * Updates a {@link Requisition} by adding a {@link Requisition} to it and saving into the backing store.
     * Checks to ensure the underlying requisition was not updated before this update was made. If so throw
     * an {@link gov.nysenate.ess.supply.requisition.exception.ConcurrentRequisitionUpdateException}.
     *
     * This check is done by comparing the {@code lastModified} requisition date time according to the update
     * with the lastModified date time of the requisition in the database. If they are not equal another update
     * took place before this one and this update will have to be resubmitted.
     * @param lastModified The requisition's last modified date time according to the update.
     * @return The requisition id.
     */
    int updateRequisition(int requisitionId, Requisition requisition, LocalDateTime lastModified);

    void undoRejection(Requisition requisition);

    Requisition getRequisitionById(int requisitionId);

    PaginatedList<Requisition> searchRequisitions(String destination, String customerId, EnumSet<RequisitionStatus> statuses,
                                                  Range<LocalDateTime> dateRange, String dateField, LimitOffset limitOffset);

    /**
     * Search a users order history.
     * Order history consists of all of a users orders plus all other orders with destination equal to the users work location.
     *
     * @param destination String representing the location id of a destination. E.g. "A42FB-W"
     * @param customerId Users employee id.
     * @param statuses {@link RequisitionStatus statuses} to include in results.
     * @param dateRange Date range to search within.
     * @param dateField
     * @param limitOffset
     * @return
     */
    PaginatedList<Requisition> searchOrderHistory(String destination, int customerId, EnumSet<RequisitionStatus> statuses,
                                                  Range<LocalDateTime> dateRange, String dateField, LimitOffset limitOffset);

}
