package gov.nysenate.ess.web.model;

import gov.nysenate.ess.web.BaseTests;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Created by riken on 3/11/14.
 */
public class TimeEntryTest extends BaseTests
{

    @Test
    public void testGetDailyTotal() throws Exception {
        BigDecimal bd = new BigDecimal("0.00");
        Assert.assertEquals(0.0,bd);
        bd.add(null);
    }
}
