package gov.nysenate.ess.supply.reports.itemsummary;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.model.RequisitionQuery;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static gov.nysenate.ess.supply.reports.itemsummary.RequisitionFilter.matchesCommodityCode;

@Service
public class ItemSummaryService {

    private RequisitionService requisitionService;

    @Autowired
    public ItemSummaryService(RequisitionService requisitionService) {
        this.requisitionService = requisitionService;
    }

    /**
     * Creates a list of {@code ItemSummary} objects from the provided filters.
     *
     * @param from
     * @param to
     * @param locationCode
     * @param commodityCode
     */
    public List<ItemSummary> createItemRequisitionSummaries(
            @NotNull LocalDateTime from,
            @NotNull LocalDateTime to,
            @Nullable String locationCode,
            @Nullable String commodityCode) {
        // Fetch Requisitions
        RequisitionQuery query = new RequisitionQuery()
                .setFromDateTime(from)
                .setToDateTime(to)
                .setLimitOffset(LimitOffset.ALL);
        List<Requisition> requisitions = requisitionService.searchRequisitions(query).getResults();

        // Apply location and item filters
        RequisitionFilter reqFilter = new RequisitionFilter();
        List<Requisition> filteredRequisitions = reqFilter
                .withItem(commodityCode)
                .withLocation(locationCode)
                .apply(requisitions);

        // Convert into List of ItemRequisitionSummary objects.
        Set<SupplyItem> distinctItems = filteredRequisitions.stream()
                .flatMap(req -> req.getLineItems().stream())
                .map(LineItem::getItem)
                .filter(item -> matchesCommodityCode(item, commodityCode))
                .collect(Collectors.toSet());

        return distinctItems.stream()
                .map(item -> ItemSummary.of(item, filteredRequisitions))
                .sorted(Comparator.comparing(irs -> irs.getItem().getCommodityCode()))
                .collect(Collectors.toUnmodifiableList());
    }
}
