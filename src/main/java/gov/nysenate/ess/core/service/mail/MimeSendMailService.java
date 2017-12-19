package gov.nysenate.ess.core.service.mail;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.config.RuntimeLevel;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Collection;

import static javax.mail.Message.RecipientType.*;

/**
 * {@inheritDoc}
 *
 * Wires the mail service into spring and provides some additional convenience methods
 */
@Service
public class MimeSendMailService extends JavaMailSenderImpl implements SendMailService
{

    private static final Logger logger = LoggerFactory.getLogger(MimeSendMailService.class);

    private static final EmailValidator emailValidator = EmailValidator.getInstance(false);
    private static final ImmutableList<Message.RecipientType> recipientTypes = ImmutableList.of(TO, CC, BCC);

    private final RuntimeLevel runtimeLevel;

    private final boolean testModeEnabled;
    private final InternetAddress testModeAddress;

    @Autowired
    public MimeSendMailService(MailUtils mailUtils,
                               @Value("${runtime.level}") String runtimeLevel,
                               @Value("${mail.test.enabled:true}") boolean testModeEnabled,
                               @Value("${mail.test.address:}") String testModeAddress
    ) {
        setSession(mailUtils.getSmtpSession());

        this.runtimeLevel = RuntimeLevel.of(runtimeLevel);
        this.testModeEnabled = testModeEnabled;
        InternetAddress address = null;
        if (testModeEnabled) {
            try {
                if (!emailValidator.isValid(testModeAddress)) {
                    throw new AddressException();
                }
                address = new InternetAddress(testModeAddress);
            } catch (AddressException e) {
                throw new IllegalArgumentException("Invalid mail test address provided: " + testModeAddress +
                        "  Add a correct address in the properties file, or turn off test mode to prevent this error.");
            }
        }
        this.testModeAddress = address;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(String to, String from, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        send(message);
    }

    /** {@inheritDoc} */
    @Override
    public void sendMessage(String to, String subject, String text) {
        sendMessage(to, null, subject, text);
    }

    /** {@inheritDoc} */
    @Override
    public MimeMessage newHtmlMessage(String to, String subject, String html) {
        MimeMessage message = createMimeMessage();
        try {
            message.setRecipient(TO, new InternetAddress(to, false));
            message.setSubject(subject);
            message.setContent(html, "text/html; charset=utf-8");
        } catch (MessagingException ex) {
            throw new EssMessagingException(ex);
        }
        return message;
    }

    /** {@inheritDoc} */
    @Override
    public void sendSimpleMessages(Collection<SimpleMailMessage> messages) {
        send(messages.toArray(new SimpleMailMessage[messages.size()]));
    }

    /** {@inheritDoc} */
    @Override
    public void sendMessages(Collection<MimeMessage> messages) {
        send(messages.toArray(new MimeMessage[messages.size()]));
    }

    /* --- Internal Methods --- */

    /**
     * Overrides {@link JavaMailSenderImpl#doSend(MimeMessage[], Object[])}
     * to intercept all outgoing messages.
     * If the runtime level is not prod, or test mode is enabled, perform some modifications to messages
     *
     * {@inheritDoc}
     */
    @Override
    protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
        if (testModeEnabled || !isProd()) {
            for (MimeMessage message : mimeMessages) {
                try {
                    debugModify(message);
                } catch (MessagingException ex) {
                    throw new EssMessagingException(ex);
                }
            }
        }
        super.doSend(mimeMessages, originalMessages);
    }

    /**
     * Perform modifications to the given {@link MimeMessage} according to any set debug options.
     *
     * @param message {@link MimeMessage}
     * @throws MessagingException
     */
    private void debugModify(MimeMessage message) throws MessagingException {
        StringBuilder debugSubject = new StringBuilder();
        if (!isProd()) {
            debugSubject.append("*").append(runtimeLevel).append("* ");
        }
        if (testModeEnabled) {
            for (Message.RecipientType type : recipientTypes) {
                // Append addresses to the subject
                Address[] recipients = message.getRecipients(type);
                if (recipients == null) {
                    continue;
                }
                for (Address addr : recipients) {
                    debugSubject.append(type).append(":").append(addr).append(" ");
                }
                // Remove addresses from message
                message.setRecipients(type, (String) null);
            }
            // Set the test address as the recipient
            message.addRecipient(TO, testModeAddress);
        }
        debugSubject.append(message.getSubject());
        message.setSubject(debugSubject.toString());
    }

    /**
     * @return true iff the current runtime level is {@link RuntimeLevel#PROD}
     */
    private boolean isProd() {
        return RuntimeLevel.PROD.equals(runtimeLevel);
    }
}