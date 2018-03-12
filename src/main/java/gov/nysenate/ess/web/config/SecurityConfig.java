package gov.nysenate.ess.web.config;

import gov.nysenate.ess.core.dao.stats.UserAgentDao;
import gov.nysenate.ess.web.security.filter.EssApiAuthenticationFilter;
import gov.nysenate.ess.web.security.filter.EssAuthenticationFilter;
import gov.nysenate.ess.web.security.filter.SessionTimeoutFilter;
import gov.nysenate.ess.web.security.session.SessionTimeoutDao;
import gov.nysenate.ess.web.security.xsrf.XsrfTokenValidator;
import gov.nysenate.ess.web.security.xsrf.XsrfValidator;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.config.Ini;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.ServletContainerSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;

/**
 * Configures dependencies necessary for security based functionality.
 * The security framework used is Apache Shiro (http://shiro.apache.org/).
 */
@Configuration
public class SecurityConfig
{
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final String loginUrl;
    private final String loginSuccessUrl;
    private final int xsrfBytesSize;

    private final UserAgentDao userAgentDao;
    private final SessionTimeoutDao sessionTimeoutDao;

    @Autowired
    public SecurityConfig(UserAgentDao userAgentDao,
                          SessionTimeoutDao sessionTimeoutDao,
                          @Value("${login.url:/login}") String loginUrl,
                          @Value("${login.success.url:/}") String loginSuccessUrl,
                          @Value("${xsrf.token.bytes:128}") int xsrfBytesSize) {
        this.userAgentDao = userAgentDao;
        this.sessionTimeoutDao = sessionTimeoutDao;
        this.loginUrl = loginUrl;
        this.loginSuccessUrl = loginSuccessUrl;
        this.xsrfBytesSize = xsrfBytesSize;
    }

    /**
     * Shiro Filter factory that sets up the url authentication mechanism and applies the security
     * manager instance.
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter() {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager());
        shiroFilter.setLoginUrl(loginUrl);
        shiroFilter.setSuccessUrl(loginSuccessUrl);
        shiroFilter.setFilterChainDefinitionMap(shiroIniConfig().getSection("urls"));
        return shiroFilter;
    }

    /**
     * Configures the shiro security manager with the instance of the active realm.
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setCacheManager(shiroCacheManager());
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }

    /**
     * Configures session manager.
     * @return {@link WebSessionManager}
     */
    @Bean(name = "sessionManager")
    public WebSessionManager sessionManager() {
        ServletContainerSessionManager sessionManager = new ServletContainerSessionManager();
        return sessionManager;
    }

    @Bean(name = "shiroCacheManager")
    public CacheManager shiroCacheManager() {
        return new MemoryConstrainedCacheManager();
    }

    /**
     * This is needed for Shiro annotations to work
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager());
        return advisor;
    }

    /**
     * Filter implementation used for authentication. This bean is automatically detected by the
     * ShiroFilterFactoryBean instance and can be used in the filter chain definitions by referencing
     * the bean name.
     */
    @Bean(name = "essAuthc")
    public Filter essAuthenticationFilter () {
        return new EssAuthenticationFilter(userAgentDao);
    }

    /**
     * An access control filter for session timeouts.
     * If the user's session is timed out, they will be logged out and redirected to login.
     * This filter should only be used for pages.  Api session timeouts are baked into the api authc filter.
     * @return
     */
    @Bean(name = "sessionTimeoutFilter")
    public Filter sesssionTimeoutFilter () {
        return new SessionTimeoutFilter(sessionTimeoutDao);
    }

    /**
     * Filter implementation used for API authentication. This bean is automatically detected by the
     * ShiroFilterFactoryBean instance and can be used in the filter chain definitions by referencing
     * the bean name as seen in shiro.ini.
     */
    @Bean(name = "essApiAuthc")
    public Filter essApiAuthenticationFilter() {
        return new EssApiAuthenticationFilter(sessionTimeoutDao);
    }

    /**
     * XsrfValidator implementation instance.
     */
    @Bean(name = "xsrfValidator")
    @DependsOn("properties")
    public XsrfValidator xsrfValidator() {
        return new XsrfTokenValidator(xsrfBytesSize);
    }

    /**
     * Exposes the shiro.ini configuration file as an Ini instance that is consumed by the
     * security filter manager when setting up the filter chains.
     */
    public Ini shiroIniConfig() {
        return Ini.fromResourcePath("classpath:shiro.ini");
    }
}
