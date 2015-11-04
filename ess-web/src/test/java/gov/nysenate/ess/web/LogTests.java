package gov.nysenate.ess.web;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(LogTests.class);

    @Test
    public void logTest() {
        logger.info("This is a debug log!");
    }
}
