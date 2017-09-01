package gov.nysenate.ess.core.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Collection;

/**
 * {@inheritDoc}
 *
 * Wires the mail service into spring and provides some additional convenience methods
 */
@Service
public class MimeSendMailService extends JavaMailSenderImpl implements SendMailService
{
    @Autowired private MailUtils mailUtils;
    @Value("${runtime.level}") private String runtimeLevel;

    @PostConstruct
    public void init() {
        setSession(mailUtils.getSmtpSession());
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
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        send(message);
    }

    /** {@inheritDoc} */
    @Override
    public MimeMessage newHtmlMessage(String to, String subject, String html) {
        MimeMessage message = createMimeMessage();
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to, false));
            subject = addRuntimeInfo(subject);
            message.setSubject(subject);
            message.setContent(html, "text/html; charset=utf-8");
        } catch (MessagingException ex) {
            throw new EssMessagingException(ex);
        }
        return message;
    }

    /**
     * Prepend runtime info to email subjects if dev or test.
     */
    private String addRuntimeInfo(String subject) {
        if (runtimeLevel.equalsIgnoreCase("dev")) {
            return "*** DEV *** " + subject;
        }
        if (runtimeLevel.equalsIgnoreCase("test")) {
            return "*** TEST *** " + subject;
        }
        return subject;
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
}