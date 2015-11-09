package gov.nysenate.ess.web;

import gov.nysenate.ess.core.BaseTests;
import gov.nysenate.ess.web.config.WebApplicationConfig;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * This class contains the annotations necessary to bootstrap the application context
 * with the Spring Sample Runner. In order to make writing test classes easier, simply
 * extend this class and the context will be all set up.
 */
@WebAppConfiguration
@ContextConfiguration(classes = {WebApplicationConfig.class})
public abstract class WebTests extends BaseTests
{
    protected MockMvc mockMvc;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected WebApplicationContext wac;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }
}
