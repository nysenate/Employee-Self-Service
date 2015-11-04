package gov.nysenate.ess.core.service.mail;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;
import javax.mail.*;

/**
 * Contains methods that can be used to interact with mail servers
 */
@Service
public class MailUtils
{
    @Value("${mail.smtp.host}") private String host;
    @Value("${mail.smtp.port}") private String port;
    @Value("${mail.smtp.auth:false}") private boolean auth;
    @Value("${mail.smtp.user:}") private String smtpUser;
    @Value("${mail.smtp.password:}") private String smtpPass;
    @Value("${mail.debug:false}") private boolean debug;
    @Value("${mail.smtp.starttls.enable:}") private boolean stlsEnable;

    private Properties mailProperties;

    @PostConstruct
    public void init() {
        mailProperties = new Properties();
        mailProperties.put("mail.smtp.host", host);
        mailProperties.put("mail.smtp.port", port);
        mailProperties.put("mail.smtp.auth", auth);
        mailProperties.put("mail.smtp.starttls.enable", stlsEnable);
        mailProperties.put("mail.smtp.user", smtpUser);
        mailProperties.put("mail.smtp.pass", smtpPass);
        mailProperties.put("mail.debug", debug);
    }

    /**
     * Gets an authenticated smtp mail session
     * @return Session
     */
    public Session getSmtpSession() {
        return Session.getInstance(mailProperties, getSmtpAuthenticator());
    }

    /**
     * Gets a mail session for use with imaps
     * @return Session
     */
    public Session getImapsSession() {
        return Session.getInstance(mailProperties);
    }

    public Properties getMailProperties() {
        return mailProperties;
    }

    /** --- Internal Methods --- */

    /**
     * Generates an authenticator from the smtp properties
     */
    private Authenticator getSmtpAuthenticator() {
        return new Authenticator() {
            private PasswordAuthentication pa = new PasswordAuthentication(smtpUser, smtpPass);
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return pa;
            }
        };
    }

}
