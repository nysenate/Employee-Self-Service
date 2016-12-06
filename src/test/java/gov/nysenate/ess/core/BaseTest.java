package gov.nysenate.ess.core;

import gov.nysenate.ess.web.config.WebApplicationConfig;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({
        @ContextConfiguration(classes = {TestConfig.class}),
        @ContextConfiguration(classes = {WebApplicationConfig.class})
})
@ActiveProfiles("test")
public abstract class BaseTest {
}
