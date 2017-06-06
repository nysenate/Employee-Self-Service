package gov.nysenate.ess.core.config;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Transactional
@Category(SillyTest.class)
public class TransactionConfigIT extends BaseTest {

    @Resource(name = "localJdbcTemplate") JdbcTemplate localJdbcTemplate;
    @Resource(name = "remoteJdbcTemplate") JdbcTemplate remoteJdbcTemplate;

    @Test
    @Transactional(value = DatabaseConfig.localTxManager)
    public void localRollbackTest1() {
        localInsert();
    }

    @Test
    @Transactional(value = DatabaseConfig.localTxManager)
    public void localRollbackTest2() {
        localInsert();
    }

    @Test(expected = AssertionError.class)
    @Transactional(value = DatabaseConfig.localTxManager)
    public void testLocalInsert() {
        localInsert();
        localInsert();
    }

    @Test
    @Transactional(value = DatabaseConfig.remoteTxManager)
    public void remoteRollBackTest1() {
        remoteInsert();
    }

    @Test
    @Transactional(value = DatabaseConfig.remoteTxManager)
    public void remoteRollBackTest2() {
        remoteInsert();
    }

    @Test(expected = AssertionError.class)
    @Transactional(value = DatabaseConfig.remoteTxManager)
    public void testRemoteInsert() {
        remoteInsert();
        remoteInsert();
    }

    private void localInsert() {
        final String querySql = "SELECT COUNT(*) AS count\n" +
                "FROM ess.user_roles WHERE id = 999999";
        final String insertSql = "INSERT INTO ess.user_roles\n" +
                "(id, employee_id, role)\n" +
                "VALUES (999999, 9999, 'SENATE_EMPLOYEE'::ess_role)";

        testExistence(localJdbcTemplate, querySql, false);
        localJdbcTemplate.update(insertSql);
        testExistence(localJdbcTemplate, querySql, true);
    }

    private void remoteInsert() {
        final String querySql = "SELECT COUNT(*) AS count\n" +
                "FROM TS_OWNER.PM23SUPOVRRD WHERE NUXREFEM = 999999";
        final String insertSql = "INSERT INTO TS_OWNER.PM23SUPOVRRD\n" +
                "(NUXREFEM)\n" +
                "VALUES (999999)";

        testExistence(remoteJdbcTemplate, querySql, false);
        remoteJdbcTemplate.update(insertSql);
        testExistence(remoteJdbcTemplate, querySql, true);
    }

    private void testExistence(JdbcTemplate jdbcTemplate, String countQuery, boolean shouldExist) {
        List<Integer> counts = jdbcTemplate.query(countQuery, (rs, rowNum) -> rs.getInt("count"));
        assertEquals("Count query should return 1 result", 1, counts.size());

        int count = counts.get(0);
        int expectedCount = shouldExist ? 1 : 0;

        String message = "Value should " + (shouldExist ? "" : "not ") + "exist in db";

        assertEquals(message, expectedCount, count);
    }
}
