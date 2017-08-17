package gov.nysenate.ess.supply.statistics.location;

import com.google.common.collect.Range;
import com.google.common.collect.Sets;
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

    /**
     * For each location, get the quantities of each item ordered in the given {@code year} and {@code month}.
     *
     * Included in the calculated totals are all items/locations from non rejected requisitions ordered in the
     * given month plus requisitions ordered in previous months which have not yet been approved. These additional
     * requisitions will likely be approved this month, and therefore their item counts should be included in
     * this months totals.
     *
     * @param year a 4 digit int representing the year to get quantities for.
     * @param month a integer from 1 - 12 representing the month to get quantities for.
     * @return A list of {@code LocationStatistic}'s.
     */
    public List<LocationStatistic> getAllLocationStatistics(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month value is invalid, must be 1-12.");
        }

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);

        Set<Requisition> monthlyRequisitions = monthlyRequisitions(start, end);
        Set<Requisition> inProgressRequisitions = inProgressRequisitions(start);
        monthlyRequisitions.addAll(inProgressRequisitions);

        Set<Location> locations = distinctDestinations(monthlyRequisitions);
        return locations.stream()
                        .map(loc -> new LocationStatistic(loc, monthlyRequisitions))
                        .collect(Collectors.toList());
    }

    /**
     * Get all non rejected requisitions with an orderedDateTime in the given month.
     */
    private Set<Requisition> monthlyRequisitions(LocalDateTime monthStart, LocalDateTime monthEnd) {
        return Sets.newHashSet(requisitionService.searchRequisitions("All", "All",
                EnumSet.complementOf(EnumSet.of(RequisitionStatus.REJECTED)),
                Range.closed(monthStart, monthEnd),
                "ordered_date_time",
                "All", LimitOffset.ALL, "All", "All").getResults());
    }

    /**
     * Gets not yet approved requisitions from previous months.
     */
    private Set<Requisition> inProgressRequisitions(LocalDateTime monthStart) {
        return Sets.newHashSet(requisitionService.searchRequisitions("All", "All",
                EnumSet.of(RequisitionStatus.PENDING, RequisitionStatus.PROCESSING, RequisitionStatus.COMPLETED),
                Range.closed(monthStart.minusYears(1), monthStart),
                "ordered_date_time",
                "All", LimitOffset.ALL, "All", "All").getResults());
    }

    private Set<Location> distinctDestinations(Set<Requisition> requisitions) {
        return requisitions.stream().map(Requisition::getDestination).collect(Collectors.toSet());
    }
}
