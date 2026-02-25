package gov.nysenate.ess.supply;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.requisition.dao.RequisitionDao;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.model.RequisitionQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InMemoryRequisitionDao implements RequisitionDao {
    private Map<Integer, Requisition> requisitionsById = new HashMap<>();

    @Override
    public Requisition saveRequisition(Requisition requisition) {
        requisition = requisition.setModifiedDateTime(LocalDateTime.now());
        requisitionsById.put(requisition.getRequisitionId(), requisition);
        return requisition;
    }

    @Override
    public Optional<Requisition> getRequisitionById(int requisitionId) {
        return Optional.ofNullable(requisitionsById.get(requisitionId));
    }

    @Override
    public PaginatedList<Requisition> searchRequisitions(RequisitionQuery query) {
        return new PaginatedList<>(
                requisitionsById.size(),
                LimitOffset.ALL,
                requisitionsById.values().stream().collect(Collectors.toList())
        );
    }

    @Override
    public PaginatedList<Requisition> searchOrderHistory(RequisitionQuery query) {
        return null;
    }

    @Override
    public ImmutableList<Requisition> getRequisitionHistory(int requisitionId) {
        return null;
    }

    @Override
    public void savedInSfms(int requisitionId, boolean succeed) {
        Requisition updated = requisitionsById.get(requisitionId).setSavedInSfms(succeed);
        saveRequisition(updated);
    }
}
