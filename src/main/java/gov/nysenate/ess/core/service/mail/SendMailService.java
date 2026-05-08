package gov.nysenate.ess.core.service.mail;

import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.internet.MimeMessage;
import java.util.Collection;

/**
 * An extension of {@link JavaMailSender} that provides some additional convenience methods
 */
public interface SendMailService extends JavaMailSender
{
    /**
     * Will send an HTML body email to all comma seperated values in the report.email app property
     *
     * @param subject String - subject
     * @param html String - html body content
     */
    void sendHTMLMessageToReportEmails(String subject, String html);

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
     * Sends each of the given MIME messages
     * @param messages {@link Collection<MimeMessage>} - the messages to send
     */
    void sendMessages(Collection<MimeMessage> messages);
}
