package gov.nysenate.ess.supply.requisition.service;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.model.RequisitionQuery;

import java.util.Optional;

public interface RequisitionService {

    /**
     * Handles events for, and saves, a new requisition order.
     * @param requisition The new requisition
     * @return The saved requisition with its requisitionId set.
     */
    Requisition submitRequisition(Requisition requisition);

    Requisition saveRequisition(Requisition requisition);

    Requisition processRequisition(Requisition requisition);

    /**
     * Returns the requisition state to its previous state, if possible.
     */
    Requisition undoRequisition(Requisition requisition);

    Requisition rejectRequisition(Requisition requisition);

    Optional<Requisition> getRequisitionById(int requisitionId);

    PaginatedList<Requisition> searchRequisitions(RequisitionQuery query);
    /**
     * Search a users order history.
     * Order history consists of all of a users orders plus all other orders with destination equal to the users work location.
     *
     *
     * @param query@return
     */
    PaginatedList<Requisition> searchOrderHistory(RequisitionQuery query);

    ImmutableList<Requisition> getRequisitionHistory(int requisitionId);

    /**
     * Marks a requisition as being saved in sfms.
     */
    void savedInSfms(int requisitionId, boolean succeed);

    Requisition reconcileRequisition(Requisition requisition);
}
