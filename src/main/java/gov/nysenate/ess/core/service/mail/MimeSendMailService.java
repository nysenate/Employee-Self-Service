package gov.nysenate.ess.core.service.mail;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.config.RuntimeLevel;
import org.apache.commons.validator.routines.EmailValidator;
import org.jetbrains.annotations.NotNull;
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
 * <p>
 * Wires the mail service into spring and provides some additional convenience methods
 */
@Service
public class MimeSendMailService extends JavaMailSenderImpl implements SendMailService {

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(String to, String subject, String text) {
        sendMessage(to, null, subject, text);
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendSimpleMessages(Collection<SimpleMailMessage> messages) {
        send(messages.toArray(new SimpleMailMessage[messages.size()]));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessages(Collection<MimeMessage> messages) {
        send(messages.toArray(new MimeMessage[messages.size()]));
    }

    /* --- Internal Methods --- */

    /**
     * Overrides {@link JavaMailSenderImpl#doSend(MimeMessage[], Object[])}
     * to intercept all outgoing messages.
     * If the runtime level is not prod, or test mode is enabled, perform some modifications to messages
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void doSend(@NotNull MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
        logEmails(mimeMessages);
        if (testModeEnabled) {
            prependRecipientsToSubjects(mimeMessages);
            overrideRecipients(mimeMessages);
        }
        if (!isProd()) {
            prependEnvironmentToSubjects(mimeMessages);
        }
        super.doSend(mimeMessages, originalMessages);
    }

    private void logEmails(MimeMessage[] mimeMessages) {
        try {
            for (MimeMessage message : mimeMessages) {
                for (Message.RecipientType type : recipientTypes) {
                    Address[] recipients = message.getRecipients(type);
                    if (recipients == null) {
                        continue;
                    }
                    for (Address address : recipients) {
                        logger.info("Email event triggered for Environment: {}, with testModeEnable = {}, address: {}:{}, subject: {}",
                                runtimeLevel, testModeEnabled, type, address, message.getSubject());
                    }
                }

            }
        } catch (MessagingException ex) {
            throw new EssMessagingException(ex);
        }
    }

    /**
     * Prepends the current environment name to the subject for each message.
     */
    private void prependEnvironmentToSubjects(MimeMessage[] mimeMessages) {
        for (MimeMessage message : mimeMessages) {
            try {
                message.setSubject(String.format("*%s* %s", runtimeLevel, message.getSubject()));
            } catch (MessagingException ex) {
                throw new EssMessagingException(ex);
            }
        }
    }

    /**
     * Prepends the recipients to the subject.
     * Used to know the intended destination of a message while testModeEnable=true.
     */
    private void prependRecipientsToSubjects(MimeMessage[] mimeMessages) {
        for (MimeMessage message : mimeMessages) {
            try {
                StringBuilder newSubject = new StringBuilder();
                for (Message.RecipientType type : recipientTypes) {
                    // Append addresses to the subject
                    Address[] recipients = message.getRecipients(type);
                    if (recipients == null) {
                        continue;
                    }
                    for (Address addr : recipients) {
                        newSubject.append(type).append(":").append(addr).append(" ");
                    }
                }
                newSubject.append(message.getSubject());
                message.setSubject(newSubject.toString());
            } catch (MessagingException ex) {
                throw new EssMessagingException(ex);
            }
        }
    }

    /**
     * Overrides the recipients on the given mimeMessages.
     * Removes all recipients and adds the testModeAddress as the only recipient.
     * This prevents real users from getting emails from our test environments.
     *
     * @param mimeMessages
     */
    private void overrideRecipients(MimeMessage[] mimeMessages) {
        for (MimeMessage message : mimeMessages) {
            try {
                // Clear all recipients.
                for (Message.RecipientType type : recipientTypes) {
                    message.setRecipients(type, (String) null);
                }
                // Add test address as the only recipient.
                message.addRecipient(TO, testModeAddress);
            } catch (MessagingException ex) {
                throw new EssMessagingException(ex);
            }
        }
    }

    /**
     * @return true iff the current runtime level is {@link RuntimeLevel#PROD}
     */
    private boolean isProd() {
        return RuntimeLevel.PROD.equals(runtimeLevel);
    }
}