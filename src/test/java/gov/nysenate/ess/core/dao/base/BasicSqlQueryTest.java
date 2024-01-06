package gov.nysenate.ess.core.dao.base;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.OrderBy;
import gov.nysenate.ess.core.util.SortOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class BasicSqlQueryTest
{
    static final String TWO_SCHEMA_SQL = "SELECT * FROM ${sampleSchema}.T1, ${sampleSchema2}.T2";

    BasicSqlQuery basicSqlQueryPostgres = new BasicSqlQuery()
    {
        @Override
        public String getSql() {
            return TWO_SCHEMA_SQL;
        }
        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    };

    BasicSqlQuery basicSqlQueryOracle = new BasicSqlQuery()
    {
        @Override
        public String getSql() {
            return TWO_SCHEMA_SQL;
        }
        @Override
        public DbVendor getVendor() {
            return DbVendor.ORACLE_10g;
        }
    };

    /** Verify that the schema substitution works. */
    @Test
    public void testGetSqlUsingSchemaMap() {
        Map<String, String> schemaMap = new HashMap<>();
        schemaMap.put("sampleSchema", "MEOW");
        schemaMap.put("sampleSchema2", "WOOF");
        assertEquals("SELECT * FROM MEOW.T1, WOOF.T2", basicSqlQueryPostgres.getSql(schemaMap));
        assertEquals("SELECT * FROM MEOW.T1, WOOF.T2", basicSqlQueryOracle.getSql(schemaMap));
    }

    /** Limit offset and order by should be added as well. */
    @Test
    public void testGetSqlUsingSchemaMap_And_LimitOffsetWithOrderBy() {
        Map<String, String> schemaMap = new HashMap<>();
        schemaMap.put("sampleSchema", "MEOW");
        schemaMap.put("sampleSchema2", "WOOF");
        LimitOffset limOff = new LimitOffset(10,10);
        OrderBy orderBy = new OrderBy("col1", SortOrder.ASC);
        assertEquals("SELECT * FROM MEOW.T1, WOOF.T2\nORDER BY col1 ASC LIMIT 10 OFFSET 9",
                basicSqlQueryPostgres.getSql(schemaMap, orderBy, limOff));
        // also test oracle
        String oracleLimitTemplate =
            "SELECT * FROM (SELECT ROWNUM AS rn, q.* FROM (%s) q)\n" +
            "WHERE rn >= %d AND rn <= %d";
        assertEquals(String.format(oracleLimitTemplate, "SELECT * FROM MEOW.T1, WOOF.T2\nORDER BY col1 ASC", 10, 19),
                basicSqlQueryOracle.getSql(schemaMap, orderBy, limOff));
    }
}