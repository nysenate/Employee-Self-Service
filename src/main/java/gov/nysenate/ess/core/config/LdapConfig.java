package gov.nysenate.ess.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.*;

@Configuration
public class LdapConfig
{
    private static final Logger logger = LoggerFactory.getLogger(LdapConfig.class);

    @Value("${ldap.url}") private String ldapUrl;

    @Value("${ldap.user.dn}") private String ldapUserDn;
    @Value("${ldap.user.pass}") private String ldapUserPass;
    @Value("${ldap.base}") private String ldapBase;

    /**
     * Provides a configured LdapTemplate instance that can be used to perform any ldap based operations
     * against the Senate LDAP. This should typically be autowired into DAO layer classes.
     */
    @Bean(name = "ldapTemplate")
    public LdapTemplate ldapTemplate() {
        if (ldapUrl == null || ldapUrl.isEmpty()) {
            throw new BeanInitializationException("Cannot instantiate LDAP Template because ldap.url in the properties file is not set.");
        }
        logger.info("Configuring ldap template with url {}", ldapUrl);
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl(ldapUrl);
        ldapContextSource.setBase(ldapBase);
        ldapContextSource.setUserDn(ldapUserDn);
        ldapContextSource.setPassword(ldapUserPass);
//        ldapContextSource.setAnonymousReadOnly(true);
        ldapContextSource.afterPropertiesSet();
        return new LdapTemplate(ldapContextSource);
    }
}