package gov.nysenate.ess.web.config;

import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.web.security.filter.EssAuthenticationFilter;
import gov.nysenate.ess.web.security.realm.EssLdapDbAuthzRealm;
import gov.nysenate.ess.web.security.xsrf.XsrfValidator;
import gov.nysenate.ess.web.security.xsrf.XsrfTokenValidator;
import org.apache.shiro.config.Ini;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.servlet.Filter;

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

    @Autowired private Environment environment;

    @Autowired
    private EssLdapDbAuthzRealm essAuthzRealm;

    /**
     * Shiro Filter factory that sets up the url authentication mechanism and applies the security
     * manager instance.
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter() {
        logger.info("{}", OutputUtils.toJson(environment.getActiveProfiles()));
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
    public WebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(essAuthzRealm);
        return securityManager;
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
     * XsrfValidator implementation instance.
     */
    @Bean(name = "xsrfValidator")
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

    /**
     * Basic realm implementation that provides hardcoded user and password for testing various roles.
     * This realm should NEVER be used in a production environment.
     */
    @Bean(name = "simpleRealm")
    public Realm senateSimpleRealm() {
        SimpleAccountRealm realm = new SimpleAccountRealm();
        realm.addRole("employee");
        realm.addRole("supervisor");
        realm.addRole("personnel");
        realm.addRole("admin");
        realm.addAccount("user", "pass", "employee", "supervisor", "personnel", "admin");
        return realm;
    }
}
