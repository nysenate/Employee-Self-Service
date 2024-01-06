package gov.nysenate.ess.core.dao.base;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.OrderBy;
import gov.nysenate.ess.core.util.SortOrder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Common utility methods to be used by enums/classes that store sql queries.
 */
public abstract class SqlQueryUtils
{
    static final Integer ORACLE_MAX_ROW_LIMIT = 100000;

    /**
     * Return the sql with schemas substituted.
     */
    public static String substituteSchema(Map<String, String> schemaMap, String sql) {
        return new StringSubstitutor(schemaMap).replace(sql);
    }

    /**
     * Wraps the input sql query with a limit offset clause. The returned query will have the
     * proper syntax according to the supplied 'vendor'.
     *
     * @param sql String - The original sql query
     * @param limitOffset LimitOffset - Limit/offset values should be set here
     * @param vendor DbVendor - Used for determining the syntax of the limit clause.
     * @return String
     */
    public static String withLimitOffsetClause(String sql, LimitOffset limitOffset, DbVendor vendor) {
        String limitClause = "";
        sql = (sql == null) ? "" : sql;
        if (limitOffset != null && limitOffset != LimitOffset.ALL) {
            // If the database supports the LIMIT x OFFSET n clause, it's pretty simple.
            if (vendor.supportsLimitOffset()) {
                if (limitOffset.hasLimit()) {
                    limitClause = String.format(" LIMIT %d", limitOffset.getLimit());
                }
                if (limitOffset.hasOffset()) {
                    limitClause += String.format(" OFFSET %d", limitOffset.getOffsetStart() - 1);
                }
                return sql + limitClause;
            }
            // Otherwise use ORACLE's subquery approach
            else {
                Integer start = (limitOffset.hasOffset()) ? limitOffset.getOffsetStart() : 1;
                Integer end = (limitOffset.hasLimit()) ? start + limitOffset.getLimit() - 1 : ORACLE_MAX_ROW_LIMIT;
                return String.format(
                    "SELECT * FROM (SELECT ROWNUM AS rn, q.* FROM (%s) q)\n" +
                    "WHERE rn >= %s AND rn <= %s", sql, start, end);
            }
        }
        return sql;
    }

    /**
     * Wraps the input sql query with an ORDER BY clause that is generated via the given 'orderBy' instance.
     * Ordering of multiple column names is also supported.
     *
     * @param sql String - The original sql query
     * @param orderBy OrderBy - Order by columns and sort orders should be set here.
     * @return String
     */
    public static String withOrderByClause(String sql, OrderBy orderBy) {
        String clause = "";
        if (orderBy != null) {
            ImmutableMap<String, SortOrder> sortColumns = orderBy.getSortColumns();
            List<String> orderClauses = new ArrayList<>();
            for (String column : sortColumns.keySet()) {
                if (!sortColumns.get(column).equals(SortOrder.NONE)) {
                    orderClauses.add(column + " " + sortColumns.get(column).name());
                }
            }
            if (!orderClauses.isEmpty()) {
                clause += "\nORDER BY " + StringUtils.join(orderClauses, ", ");
            }
        }
        if (sql == null) sql = "";
        return sql + clause;
    }
}