package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.util.SortOrder;

public class EmpTaskSort {

    private final EmpTaskOrderBy orderBy;
    private final SortOrder sortOrder;

    public EmpTaskSort(EmpTaskOrderBy orderBy, SortOrder sortOrder) {
        this.orderBy = orderBy;
        this.sortOrder = sortOrder;
    }

    public EmpTaskOrderBy getOrderBy() {
        return orderBy;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }
}
