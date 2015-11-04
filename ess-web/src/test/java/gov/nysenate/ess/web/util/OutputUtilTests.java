package gov.nysenate.ess.web.util;

import gov.nysenate.ess.core.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class OutputUtilTests
{
    private static final Logger logger = LoggerFactory.getLogger(OutputUtilTests.class);

    private static class SimpleObject {
        public String stringField = "string";
        public int intField = 1;
        public boolean booleanField = true;
        public List<String> listField = Arrays.asList("simple", "list");
    }

    @Test
    public void printObjectSimple() {
        SimpleObject s = new SimpleObject();
        String output = OutputUtils.toJson(s);
        assertNotNull(output);
    }

    @Test
    public void testToXml() throws Exception {
        SimpleObject s = new SimpleObject();
        String output = OutputUtils.toXml(s);
        assertNotNull(output);
        logger.info(output);
    }
}