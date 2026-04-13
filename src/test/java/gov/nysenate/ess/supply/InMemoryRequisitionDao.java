package gov.nysenate.ess.supply;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.requisition.dao.RequisitionDao;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.model.RequisitionQuery;
import gov.nysenate.ess.supply.requisition.model.SyncStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public void saveRequisitionMetadata(Requisition requisition) {
        return;
    }

    @Override
    public Optional<Requisition> getRequisitionById(int requisitionId) {
        return Optional.ofNullable(requisitionsById.get(requisitionId));
    }

    @Override
    public PaginatedList<Requisition> searchRequisitions(RequisitionQuery query) {
        List<Requisition> filtered = new ArrayList<>();
        for (Requisition requisition : requisitionsById.values()) {
            if (!query.getStatuses().contains(requisition.getStatus())) {
                continue;
            }
            if (!syncStatusComplete(query, requisition)) {
                continue;
            }
            if (!matchesDateRange(query, requisition)) {
                continue;
            }
            filtered.add(requisition);
        }
        int total = filtered.size();
        List<Requisition> paged = LimitOffset.limitList(filtered, query.getLimitOffset());
        return new PaginatedList<>(total, query.getLimitOffset(), paged);
    }

    @Override
    public PaginatedList<Requisition> searchOrderHistory(RequisitionQuery query) {
        return null;
    }

    @Override
    public ImmutableList<Requisition> getRequisitionHistory(int requisitionId) {
        return null;
    }


    private boolean syncStatusComplete(RequisitionQuery query, Requisition requisition) {
        return requisition.getSfmsSyncStatus().equals(SyncStatus.COMPLETE);
    }

    private boolean matchesDateRange(RequisitionQuery query, Requisition requisition) {
        Optional<LocalDateTime> dateValue = getDateFieldValue(query.getDateField(), requisition);
        if (!dateValue.isPresent()) {
            return false;
        }
        LocalDateTime dateTime = dateValue.get();
        return !dateTime.isBefore(query.getFromDateTime()) && !dateTime.isAfter(query.getToDateTime());
    }

    private Optional<LocalDateTime> getDateFieldValue(String dateField, Requisition requisition) {
        switch (dateField) {
            case "ordered_date_time":
                return Optional.of(requisition.getOrderedDateTime());
            case "processed_date_time":
                return requisition.getProcessedDateTime();
            case "completed_date_time":
                return requisition.getCompletedDateTime();
            case "approved_date_time":
                return requisition.getApprovedDateTime();
            case "rejected_date_time":
                return requisition.getRejectedDateTime();
            default:
                throw new IllegalArgumentException("Unsupported date field: " + dateField);
        }
    }
}
