package gov.nysenate.ess.supply.statistics;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.service.SupplyRequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

@Service
public class SupplyStatisticsService {

    @Autowired private SupplyRequisitionService requisitionService;

    public ItemStatistic getItemStatistics(String locationId, int year, int month) {
        if (locationId == null) {
            throw new IllegalArgumentException("Cannot calculate statistics for a null location.");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month value is invalid, must be 1-12.");
        }

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        List<Requisition> requisitions = requisitionService.searchRequisitions(locationId.toString(), "All",
                                                                               EnumSet.allOf(RequisitionStatus.class),
                                                                               Range.closed(start, end),
                                                                               "ordered_date_time",
                                                                               "All", LimitOffset.ALL, "All").getResults();
        return new ItemStatistic(requisitions);
    }
}
