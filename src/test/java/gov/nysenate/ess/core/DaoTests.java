package gov.nysenate.ess.core;

import gov.nysenate.ess.core.config.DaoConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.PostConstruct;

@ContextConfiguration(classes = DaoConfig.class)
public abstract class DaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(DaoTests.class);

    @PostConstruct
    public void init() {
        logger.debug("Initialized DaoTests");
    }
}
