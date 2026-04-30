package gov.nysenate.ess.web;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.apache.shiro.env.BasicIniEnvironment;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.apache.shiro.env.Environment;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * This class contains the annotations necessary to bootstrap the application context
 * with the Spring Sample Runner. In order to make writing test classes easier, simply
 * extend this class and the context will be all set up.
 */
public abstract class WebTest extends BaseTest
{
    private static final Logger logger = LoggerFactory.getLogger(WebTest.class);

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected WebApplicationContext wac;
    @Autowired
    private EmployeeInfoService empInfoService;

    protected MockMvc mockMvc;

    @Value("${auth.master.pass}") protected String masterPass;

    protected String testUid = null;
    protected MockHttpSession authenticatedSession = null;


    @Before
    public void setup() {
        logger.info("Setting up WebTest with Shiro filter integration");

        // Get the Shiro filter from the application context
        AbstractShiroFilter shiroFilter = wac.getBean("shiroFilter", AbstractShiroFilter.class);
        logger.info("Got shiroFilter: {}", shiroFilter.getClass().getName());

        // Setup MockMvc with Shiro filter
        this.mockMvc = webAppContextSetup(this.wac)
            .addFilters(shiroFilter)
            .build();

        // Setup Shiro SecurityManager for thread context
        Environment env = new BasicIniEnvironment("classpath:shiro.ini");
        SecurityManager securityManager = env.getSecurityManager();
        ThreadContext.bind(securityManager);

        // Get a test user from active employees
        int anyEmpId = empInfoService.getActiveEmpIds().stream().findFirst().get();
        Employee anyEmp = empInfoService.getEmployee(anyEmpId);
        testUid = anyEmp.getUid();
    }

    @After
    public void cleanup() {
        // Clean up the authenticated session after each test
        if (authenticatedSession != null) {
            authenticatedSession.invalidate();
            authenticatedSession = null;
        }
    }

    /**
     * Performs login via MockMvc POST to /login and stores the authenticated session.
     * This creates a real authenticated session that can be used in subsequent requests.
     *
     * @param username Username for login
     * @param password Password for login
     */
    protected void loginUser(String username, String password) throws Exception {
        logger.info("Performing login for user: {}", username);

        // Create a new session for this login
        MockHttpSession session = new MockHttpSession();

        MvcResult loginResult = mockMvc.perform(post("/login")
                .param("username", username)
                .param("password", password)
                .session(session))
                .andReturn();

        // Store the session for subsequent requests
        this.authenticatedSession = (MockHttpSession) loginResult.getRequest().getSession();
        logger.info("Login completed for user: {}", username);
    }

    /**
     * Convenience method for tests that need authenticated access.
     * Uses test credentials that should work in development environments.
     */
    protected void authenticateTestUser() throws Exception {
        if (authenticatedSession == null) {
            // Use development test credentials - these should be configured in your test environment
            loginUser(testUid, masterPass);
        }
    }

    /**
     * Performs an authenticated request using MockMvc with the authenticated session.
     * Automatically performs login if not already authenticated.
     *
     * @param requestBuilder The request to perform
     * @return ResultActions for further assertions
     */
    protected ResultActions performAuthenticated(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        authenticateTestUser();
        return mockMvc.perform(requestBuilder.session(authenticatedSession));
    }
}
