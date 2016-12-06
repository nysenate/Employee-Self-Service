package gov.nysenate.ess.web;

import gov.nysenate.ess.core.annotation.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Category(SillyTest.class)
public class LogTest extends WebTest
{
    private static final Logger logger = LoggerFactory.getLogger(LogTest.class);

    @Test
    public void logTest() {
        logger.info("This is a debug log!");
    }
}
