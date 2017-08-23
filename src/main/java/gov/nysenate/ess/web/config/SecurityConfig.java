package gov.nysenate.ess.web.config;

import gov.nysenate.ess.web.security.filter.EssApiAuthenticationFilter;
import gov.nysenate.ess.web.security.filter.EssAuthenticationFilter;
import gov.nysenate.ess.web.security.xsrf.XsrfTokenValidator;
import gov.nysenate.ess.web.security.xsrf.XsrfValidator;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.config.Ini;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.List;

/**
 * Configures dependencies necessary for security based functionality.
 * The security framework used is Apache Shiro (http://shiro.apache.org/).
 */
@Configuration
public class SecurityConfig
{
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${login.url:/login}") private String loginUrl;
    @Value("${login.success.url:/}") private String loginSuccessUrl;
    @Value("${xsrf.token.bytes:128}") private int xsrfBytesSize;

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
        return securityManager;
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
    public Filter essAuthenticationFilter() {
        return new EssAuthenticationFilter();
    }

    /**
     * Filter implementation used for API authentication. This bean is automatically detected by the
     * ShiroFilterFactoryBean instance and can be used in the filter chain definitions by referencing
     * the bean name as seen in shiro.ini.
     */
    @Bean(name = "essApiAuthc")
    public Filter essApiAuthenticationFilter() {
        return new EssApiAuthenticationFilter();
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
