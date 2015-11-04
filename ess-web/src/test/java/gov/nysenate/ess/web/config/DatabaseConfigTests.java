package gov.nysenate.ess.web.config;

import gov.nysenate.ess.web.BaseTests;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class DatabaseConfigTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfigTests.class);

    @Resource(name = "localJdbcTemplate")
    protected NamedParameterJdbcTemplate localJdbcTemplate;

    @Resource(name = "remoteJdbcTemplate")
    protected NamedParameterJdbcTemplate remoteJdbcTemplate;

    @Test
    public void testLocalDBConnection() throws Exception {
        assertNotNull(localJdbcTemplate);
        List<Integer> res = localJdbcTemplate.query("SELECT 1", new SingleColumnRowMapper<Integer>());
        assertNotNull(res);
        assertTrue("List size is not equal to 1.", res.size() == 1);
        assertEquals(1, res.get(0).intValue());
    }

    @Test
    public void testRemoteDBConnection() throws Exception {
        assertNotNull(remoteJdbcTemplate);
        List<BigDecimal> res = remoteJdbcTemplate.query("SELECT 1 FROM DUAL", new SingleColumnRowMapper<BigDecimal>());
        assertNotNull(res);
        assertTrue("List size is not equal to 1.", res.size() == 1);
        assertEquals(1, res.get(0).intValue());
    }
}
