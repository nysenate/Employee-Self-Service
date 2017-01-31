package gov.nysenate.ess.core.service.mail;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

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
    @Value("${mail.smtp.from:noreply@nysenate.gov}") private String smtpFrom;
    @Value("${mail.smtp.starttls.enable:false}") private boolean stlsEnable;
    @Value("${mail.smtp.ssl.enable:false}") private boolean sslEnable;
    @Value("${mail.smtp.ssl.protocols}") private String sslProtocol;

    private Properties mailProperties;
    private Authenticator authenticator;

    @PostConstruct
    public void init() {
        authenticator = new EssPasswordAuthenticator(
                new PasswordAuthentication(smtpUser, smtpPass));

        mailProperties = new Properties();
        mailProperties.put("mail.smtp.host", host);
        mailProperties.put("mail.smtp.port", port);
        mailProperties.put("mail.smtp.auth", auth);
        mailProperties.put("mail.smtp.starttls.enable", stlsEnable);
        mailProperties.put("mail.smtp.ssl.enable", sslEnable);
        mailProperties.put("mail.smtp.ssl.protocols", sslProtocol);
        mailProperties.put("mail.smtp.user", smtpUser);
        mailProperties.put("mail.smtp.pass", smtpPass);
        mailProperties.put("mail.smtp.from", smtpFrom);
        mailProperties.put("mail.debug", debug);
    }

    /**
     * Gets an authenticated smtp mail session
     * @return Session
     */
    public Session getSmtpSession() {
        return Session.getInstance(mailProperties, authenticator);
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
     * Password authenticator based on smtp credentials
     */
    private static class EssPasswordAuthenticator extends Authenticator {
        private PasswordAuthentication pa;

        public EssPasswordAuthenticator(PasswordAuthentication pa) {
            this.pa = pa;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return pa;
        }
    }

}
