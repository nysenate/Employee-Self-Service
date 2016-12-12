package gov.nysenate.ess.core.service.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;
import java.util.Collection;

/**
 * An extension of {@link JavaMailSender} that provides some additional convenience methods
 */
public interface SendMailService extends JavaMailSender
{
    /**
     * Sends a simple plaintext email message constructed from basic message parameters
     *
     * @param to The intended receiver of the email
     * @param from The from address for the email
     * @param subject Subject of the email
     * @param text The email body
     */
    void sendMessage(String to, String from, String subject, String text);

    /**
     * Sends a simple plaintext email message constructed from basic message parameters
     *
     * @param to The intended receiver of the email
     * @param subject Subject of the email
     * @param text The email body
     */
    void sendMessage(String to, String subject, String text);

    /**
     * Constructs a new HTML {@link MimeMessage} using some common message parameters
     *
     * @param to String - receiver (uses TO field)
     * @param subject String - subject
     * @param html String - html body content
     * @return
     */
    MimeMessage newHtmlMessage(String to, String subject, String html);

    /**
     * Sends all email messages from the given collection of simple email messages
     * @param messages {@link Collection<SimpleMailMessage>} - the messages to send
     */
    void sendSimpleMessages(Collection<SimpleMailMessage> messages);

    /**
     * Sends each of the given MIME messages
     * @param messages {@link Collection<MimeMessage>} - the messages to send
     */
    void sendMessages(Collection<MimeMessage> messages);
}
