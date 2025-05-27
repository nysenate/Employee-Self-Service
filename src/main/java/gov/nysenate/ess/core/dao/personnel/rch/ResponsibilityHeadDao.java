package gov.nysenate.ess.core.dao.personnel.rch;

import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.core.util.SortOrder;

import java.util.List;

public interface ResponsibilityHeadDao {

    /**
     * Retrieves the {@link ResponsibilityHead} identified by the given {@code code}.
     *
     * @param code The code which uniquely identifies a ResponsibilityHead. Cannot be null or empty.
     * @return The {@link ResponsibilityHead} with the given {@code code}.
     */
    ResponsibilityHead rchForCode(String code);


    /**
     * Retrieves all {@link ResponsibilityHead}'s identified by the {@code codes} provided.
     *
     * @param codes A List of rch codes. Invalid rch codes are ignored.
     * @return A List of ResponsibilityHead's matching the provided codes. An empty list is returned
     * if {@code codes} is empty or contained all invalid codes.
     */
    List<ResponsibilityHead> rchsForCodes(List<String> codes);

    /**
     * Search for resp ctr heads whose name or code matches the given term.
     *
     * @param term String
     * @param limitOffset {@link LimitOffset}
     * @param order {@link SortOrder}
     * @return {@link PaginatedList<ResponsibilityHead>}
     */
    PaginatedList<ResponsibilityHead> rchSearch(String term, LimitOffset limitOffset, SortOrder order);
}
