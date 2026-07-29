package gov.nysenate.ess.travel.request.app;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.SortOrder;

import java.time.LocalDate;
import java.util.EnumSet;

public class TravelApplicationQuery {

    private final LocalDate from;
    private final LocalDate to;
    private final EnumSet<AppStatus> statuses;
    private final TravelApplicationSortField sortField;
    private final SortOrder sortOrder;
    private final LimitOffset limitOffset;

    public TravelApplicationQuery(LocalDate from, LocalDate to, EnumSet<AppStatus> statuses,
                                  TravelApplicationSortField sortField, SortOrder sortOrder,
                                  LimitOffset limitOffset) {
        this.from = from;
        this.to = to;
        this.statuses = statuses == null || statuses.isEmpty()
                ? EnumSet.allOf(AppStatus.class)
                : EnumSet.copyOf(statuses);
        this.sortField = sortField;
        this.sortOrder = sortOrder;
        this.limitOffset = limitOffset;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public EnumSet<AppStatus> getStatuses() {
        return EnumSet.copyOf(statuses);
    }

    public TravelApplicationSortField getSortField() {
        return sortField;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public LimitOffset getLimitOffset() {
        return limitOffset;
    }
}
