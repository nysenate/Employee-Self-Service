package gov.nysenate.ess.core.config;

import gov.nysenate.ess.core.BaseTests;
import gov.nysenate.ess.core.DaoTests;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class DatabaseConfigIT extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfigIT.class);

    /** These templates use ? as args. */
    @Resource(name = "localJdbcTemplate") JdbcTemplate localJdbcTemplate;
    @Resource(name = "remoteJdbcTemplate") JdbcTemplate remoteJdbcTemplate;

    /** These templates use :key as args. */
    @Resource(name = "localNamedJdbcTemplate") NamedParameterJdbcTemplate localNamedJdbcTemplate;
    @Resource(name = "remoteNamedJdbcTemplate") NamedParameterJdbcTemplate remoteNamedJdbcTemplate;

    @Test
    public void testLocalJdbcTemplate() throws Exception {
        assertNotNull(localJdbcTemplate);
        List<Integer> res = localJdbcTemplate.query("SELECT ?", new SingleColumnRowMapper<>(), 2);
        assertNotNull(res);
        assertTrue("List size is not equal to 1.", res.size() == 1);
        assertEquals(2, res.get(0).intValue());
    }

    @Test
    public void testLocalNamedJdbcTemplate() throws Exception {
        assertNotNull(localNamedJdbcTemplate);
        MapSqlParameterSource params = new MapSqlParameterSource("num", 2);
        List<Integer> res = localNamedJdbcTemplate.query("SELECT :num", params, new SingleColumnRowMapper<>());
        assertNotNull(res);
        assertTrue("List size is not equal to 1.", res.size() == 1);
        assertEquals(2, res.get(0).intValue());
    }

    @Test
    public void testRemoteJdbcTemplate() throws Exception {
        assertNotNull(remoteJdbcTemplate);
        Object[] args = {2};
        List<BigDecimal> res = remoteJdbcTemplate.query("SELECT ? FROM DUAL", new SingleColumnRowMapper<>(), args);
        assertNotNull(res);
        assertTrue("List size is not equal to 1.", res.size() == 1);
        assertEquals(2, res.get(0).intValue());
    }

    @Test
    public void testRemoteNamedJdbcTemplate() throws Exception {
        assertNotNull(remoteNamedJdbcTemplate);
        MapSqlParameterSource params = new MapSqlParameterSource("num", 2);
        List<BigDecimal> res = remoteNamedJdbcTemplate.query("SELECT :num FROM DUAL", params, new SingleColumnRowMapper<>());
        assertNotNull(res);
        assertTrue("List size is not equal to 1.", res.size() == 1);
        assertEquals(2, res.get(0).intValue());
    }
}