package gov.nysenate.ess.core.dao.base;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.OrderBy;
import gov.nysenate.ess.core.util.SortOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class SqlQueryUtilsTest
{
    @Test
    public void testWithLimitOffsetClause_Oracle() {
        String oracleLimitTemplate =
            "SELECT * FROM (SELECT ROWNUM AS rn, q.* FROM (%s) q)\n" +
            "WHERE rn >= %d AND rn <= %d";
        // Null sql
        String sql = SqlQueryUtils.withLimitOffsetClause(null, LimitOffset.TEN, DbVendor.ORACLE_10g);
        assertEquals(String.format(oracleLimitTemplate, "", 1, 10), sql);
        // No limit
        sql = SqlQueryUtils.withLimitOffsetClause("ABCDEF", LimitOffset.ALL, DbVendor.ORACLE_10g);
        assertEquals("ABCDEF", sql);
        // No limit, with offset
        sql = SqlQueryUtils.withLimitOffsetClause("ABCDEF", new LimitOffset(0, 10), DbVendor.ORACLE_10g);
        assertEquals(String.format(oracleLimitTemplate, "ABCDEF", 10, SqlQueryUtils.ORACLE_MAX_ROW_LIMIT), sql);
        // Limit with offset
        sql = SqlQueryUtils.withLimitOffsetClause("ABCDEF", new LimitOffset(20, 100), DbVendor.ORACLE_10g);
        assertEquals(String.format(oracleLimitTemplate, "ABCDEF", 100, 119), sql);
        // Offset 1
        sql = SqlQueryUtils.withLimitOffsetClause("ABCDEF", new LimitOffset(20, 1), DbVendor.ORACLE_10g);
        assertEquals(String.format(oracleLimitTemplate, "ABCDEF", 1, 20), sql);
    }

    /** In postgres, OFFSET means skip N number of rows before returning results. The offset part of
     * the limit offset class refers to the start position, i.e. LimitOffset(0, 10) means get results
     * from the 10th item, which means skip the first 9 items.
     */
    @Test
    public void testWithLimitOffsetClause_POSTGRES() {
        // Null sql
        String sql = SqlQueryUtils.withLimitOffsetClause(null, LimitOffset.TEN, DbVendor.POSTGRES);
        assertEquals(" LIMIT 10", sql);
        sql = SqlQueryUtils.withLimitOffsetClause(null, LimitOffset.ALL, DbVendor.POSTGRES);
        assertEquals("", sql.trim());
        // No limit
        sql = SqlQueryUtils.withLimitOffsetClause("ABCDEF", LimitOffset.ALL, DbVendor.POSTGRES);
        assertEquals("ABCDEF", sql);
        // No limit, with offset
        sql = SqlQueryUtils.withLimitOffsetClause("ABCDEF", new LimitOffset(0, 10), DbVendor.POSTGRES);
        assertEquals("ABCDEF OFFSET 9", sql);
        // Limit with offset
        sql = SqlQueryUtils.withLimitOffsetClause("ABCDEF", new LimitOffset(20, 100), DbVendor.POSTGRES);
        assertEquals("ABCDEF LIMIT 20 OFFSET 99", sql);
        // Offset 1 (no offset)
        sql = SqlQueryUtils.withLimitOffsetClause("ABCDEF", new LimitOffset(20, 1), DbVendor.POSTGRES);
        assertEquals("ABCDEF LIMIT 20", sql);
        // Offset 2 (skip 1 row)
        sql = SqlQueryUtils.withLimitOffsetClause("ABCDEF", new LimitOffset(20, 2), DbVendor.POSTGRES);
        assertEquals("ABCDEF LIMIT 20 OFFSET 1", sql);
    }

    @Test
    public void testWithOrderByClause() {
        // No order by
        String sql = SqlQueryUtils.withOrderByClause("ABCDEF", new OrderBy());
        assertEquals("ABCDEF", sql);
        // Null sql
        sql = SqlQueryUtils.withOrderByClause(null, new OrderBy("col1", SortOrder.ASC));
        assertEquals("\nORDER BY col1 ASC", sql);
        // One order by
        sql = SqlQueryUtils.withOrderByClause("ABCDEF", new OrderBy("col1", SortOrder.ASC));
        assertEquals("ABCDEF\nORDER BY col1 ASC", sql);
        // Multiple order bys
        sql = SqlQueryUtils.withOrderByClause("ABCDEF", new OrderBy("col1", SortOrder.ASC, "col2", SortOrder.DESC));
        assertEquals("ABCDEF\nORDER BY col1 ASC, col2 DESC", sql);
        // Ignore no sorts
        sql = SqlQueryUtils.withOrderByClause("ABCDEF", new OrderBy("col1", SortOrder.NONE));
        assertEquals("ABCDEF", sql);
        sql = SqlQueryUtils.withOrderByClause("ABCDEF", new OrderBy("col1", SortOrder.NONE, "col2", SortOrder.DESC,
                                                                    "col3", SortOrder.NONE));
        assertEquals("ABCDEF\nORDER BY col2 DESC", sql);
    }
}