package gov.nysenate.ess.core.dao.stats;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.stats.UserAgentInfo;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import static gov.nysenate.ess.core.dao.stats.SqlUserAgentQuery.INSERT_USER_AGENT_INFO;

/**
 * {@inheritDoc}
 */
@Repository
public class SqlUserAgentDao extends SqlBaseDao implements UserAgentDao {

    /** {@inheritDoc} */
    @Override
    public void insertUserAgentInfo(UserAgentInfo userAgentInfo) {
        localNamedJdbc.update(INSERT_USER_AGENT_INFO.getSql(schemaMap()), getUAIParams(userAgentInfo));
    }

    /* --- Internal Methods --- */

    private MapSqlParameterSource getUAIParams(UserAgentInfo userAgentInfo) {
        return new MapSqlParameterSource()
                .addValue("empId", userAgentInfo.getEmpId())
                .addValue("loginTime", toDate(userAgentInfo.getLoginTime()))
                .addValue("userAgent", userAgentInfo.getUserAgent());
    }
}
