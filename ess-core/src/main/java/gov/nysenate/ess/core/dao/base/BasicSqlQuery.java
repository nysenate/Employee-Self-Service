package gov.nysenate.ess.core.dao.base;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.OrderBy;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.Map;

public interface BasicSqlQuery
{
    /**
     * Return the sql query as defined..
     */
    public String getSql();

    /**
     * Return the sql query as is with the given schemas.
     */
    public default String getSql(Map<String, String> schemaMap) {
        StrSubstitutor strSub = new StrSubstitutor(schemaMap);
        return strSub.replace(getSql());
    }

    /**
     * Return which database this query is targeting.
     */
    public DbVendor getVendor();

    /**
     * Returns a sql query that is formatted to support the given limit offset operations.*
     */
    public default String getSql(Map<String, String> schemaMap, LimitOffset limitOffset) {
        return SqlQueryUtils.withLimitOffsetClause(getSql(schemaMap), limitOffset, getVendor());
    }

    /**
     * Returns a sql string with an order by clause set according to the supplied OrderBy instance.
     */
    public default String getSql(Map<String, String> schemaMap, OrderBy orderBy) {
        return SqlQueryUtils.withOrderByClause(getSql(schemaMap), orderBy);
    }

    /**
     * Returns a sql string with a limit offset according to the supplied LimitOffset and an
     * order by clause set according to the supplied OrderBy instance.
     */
    public default String getSql(Map<String, String> schemaMap, OrderBy orderBy, LimitOffset limitOffset) {
        return SqlQueryUtils.withLimitOffsetClause(SqlQueryUtils.withOrderByClause(getSql(schemaMap), orderBy), limitOffset, getVendor());
    }
}