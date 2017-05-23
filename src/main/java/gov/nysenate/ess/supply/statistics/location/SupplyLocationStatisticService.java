package gov.nysenate.ess.supply.statistics.location;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.model.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.service.SupplyRequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupplyLocationStatisticService {

    @Autowired private SupplyRequisitionService requisitionService;

    public List<LocationStatistic> getAllLocationStatistics(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month value is invalid, must be 1-12.");
        }
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        List<Requisition> requisitions = requisitionService.searchRequisitions("All", "All",
                                                                               EnumSet.allOf(RequisitionStatus.class),
                                                                               Range.closed(start, end),
                                                                               "ordered_date_time",
                                                                               "All", LimitOffset.ALL, "All", "All").getResults();
        Set<Location> locations = distinctDestinationsIn(requisitions);
        return locations.stream()
                        .map(loc -> new LocationStatistic(loc, requisitions))
                        .collect(Collectors.toList());
    }

    private Set<Location> distinctDestinationsIn(List<Requisition> requisitions) {
        return requisitions.stream().map(Requisition::getDestination).collect(Collectors.toSet());
    }
}
