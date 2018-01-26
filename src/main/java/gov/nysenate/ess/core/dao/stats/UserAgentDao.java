package gov.nysenate.ess.core.dao.stats;

import gov.nysenate.ess.core.model.stats.UserAgentInfo;

/**
 * Stores user agent information
 */
public interface UserAgentDao {

    /**
     * Adds the given user agent into to the repository.
     * @param userAgentInfo {@link UserAgentInfo}
     */
    void insertUserAgentInfo(UserAgentInfo userAgentInfo);
}
