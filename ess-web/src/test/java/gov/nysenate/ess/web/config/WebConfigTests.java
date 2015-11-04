package gov.nysenate.ess.web.config;

import gov.nysenate.ess.web.BaseTests;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WebConfigTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(WebConfigTests.class);

    @Autowired
    private ShiroFilterFactoryBean shiroFilter;

    @Value("${login.url:/login")
    private String loginUrl;

    @Test
    public void checkIfShiroFilterIsSet() {
        assertNotNull(shiroFilter);
    }

    @Test
    public void checkIfLoginUrlIsSet() {
        assertEquals(loginUrl, shiroFilter.getLoginUrl());
    }
}
