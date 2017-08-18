package gov.nysenate.ess.supply.requisition.dao;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.model.RequisitionQuery;

import java.util.Optional;

public interface RequisitionDao {

    Requisition saveRequisition(Requisition requisition);

    Optional<Requisition> getRequisitionById(int requisitionId);

    PaginatedList<Requisition> searchRequisitions(RequisitionQuery query);

    /**
     * Searches the order history for a employee.
     * An employees order history includes all orders for his work location plus all orders they have made themselves
     * to any other locations.
     * This is similar to the searchRequisitions method except it requires a destinationId and customerId and
     * should only be used to get an employees order history.
     */
    PaginatedList<Requisition> searchOrderHistory(RequisitionQuery query);

    ImmutableList<Requisition> getRequisitionHistory(int requisitionId);

    /**
     * Marks a requisition as saved in SFMS.
     */
    void savedInSfms(int requisitionId, boolean succeed);
}
