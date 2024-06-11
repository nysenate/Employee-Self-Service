package gov.nysenate.ess.core.service.pec.search;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;

/**
 * Service that allows for searching personnel tasks based on task and employee parameters.
 */
public interface EmpTaskSearchService {
    /**
     * Search for employees and tasks that match the given query criteria.
     *
     * @param query {@link EmpPTAQuery} query parameters
     * @param limitOffset {@link LimitOffset}
     * @return {@link PaginatedList<EmpTaskSearchService>}
     */
    PaginatedList<EmployeeTaskSearchResult> searchForEmpTasks(EmpPTAQuery query, LimitOffset limitOffset);

    /**
     * Search for employees and tasks that match the given query criteria.
     *
     * @param query {@link EmpPTAQuery} query parameters
     * @param limitOffset {@link LimitOffset}
     * @return {@link PaginatedList<EmpTaskSearchService>}
     */
    PaginatedList<EmployeeTaskSearchResult> searchForNotEmpTasks(EmpPTAQuery query, LimitOffset limitOffset);
}
