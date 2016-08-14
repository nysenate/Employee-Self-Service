package gov.nysenate.ess.supply.statistics;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LocationStatisticsCalculator {

    private RequisitionService requisitionService;
    private SupplyItemService itemService;

    @Autowired
    public LocationStatisticsCalculator(RequisitionService requisitionService, SupplyItemService itemService) {
        this.requisitionService = requisitionService;
        this.itemService = itemService;
    }

    public LocationStatistic locationStatistics(Location location, int year, int month) {
        if (location == null) {
            throw new IllegalArgumentException("Cannot calculate statistics for a null location.");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month value is invalid, must be 1-12.");
        }

        Map<String, Integer> itemOrderQuantaties = new HashMap<>();
        List<SupplyItem> items = itemService.getSupplyItems(LimitOffset.ALL).getResults();
        for (SupplyItem item : items) {
            itemOrderQuantaties.put(item.getCommodityCode(), 0);
        }

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        List<Requisition> requisitions = requisitionService.searchRequisitions(Range.closed(start, end)).getResults();
        for (Requisition requisition : requisitions) {
            for (LineItem lineItem : requisition.getLineItems()) {
                String commodityCode = lineItem.getItem().getCommodityCode();
                itemOrderQuantaties.put(commodityCode, itemOrderQuantaties.get(commodityCode) + lineItem.getQuantity());
            }
        }
        return new LocationStatistic(location, itemOrderQuantaties);
    }
}
