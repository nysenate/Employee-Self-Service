package gov.nysenate.ess.web;

import gov.nysenate.ess.core.BaseTest;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * This class contains the annotations necessary to bootstrap the application context
 * with the Spring Sample Runner. In order to make writing test classes easier, simply
 * extend this class and the context will be all set up.
 */
public abstract class WebTest extends BaseTest
{
    protected MockMvc mockMvc;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected WebApplicationContext wac;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
        SecurityManager securityManager = new IniSecurityManagerFactory("classpath:shiro.ini").getInstance();
        ThreadContext.bind(securityManager);
    }
}
