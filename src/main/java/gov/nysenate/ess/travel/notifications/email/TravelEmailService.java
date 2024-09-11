package gov.nysenate.ess.travel.notifications.email;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.travel.notifications.email.events.TravelAppEditedEmailEvent;
import gov.nysenate.ess.travel.notifications.email.events.TravelApprovalEmailEvent;
import gov.nysenate.ess.travel.notifications.email.events.TravelDisapprovalEmailEvent;
import gov.nysenate.ess.travel.notifications.email.events.TravelPendingReviewEmailEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.HashSet;
import java.util.Set;

@Service
public class TravelEmailService {

    private SendMailService sendMailService;
    private TravelAppDisapprovalEmail disapprovalEmail;
    private TravelAppApprovalEmail approvalEmail;
    private TravelAppEditEmail editEmail;
    private PendingAppReviewEmail pendingAppReviewEmail;
    private TravelEmailRecipients emailRecipients;
    private EventBus eventBus;

    @Autowired
    public TravelEmailService(SendMailService sendMailService,
                              TravelAppDisapprovalEmail disapprovalEmail,
                              TravelAppApprovalEmail approvalEmail,
                              TravelAppEditEmail editEmail,
                              PendingAppReviewEmail pendingAppReviewEmail,
                              TravelEmailRecipients emailRecipients,
                              EventBus eventBus) {
        this.sendMailService = sendMailService;
        this.disapprovalEmail = disapprovalEmail;
        this.approvalEmail = approvalEmail;
        this.editEmail = editEmail;
        this.pendingAppReviewEmail = pendingAppReviewEmail;
        this.emailRecipients = emailRecipients;
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    /**
     * Sends an email to users when their application has been approved by everyone.
     */
    @Subscribe
    public void sendApprovalEmails(TravelApprovalEmailEvent event) {
        Set<Employee> recipients = emailRecipients.forStatusUpdate(event.getApplicationReview().application());
        Set<MimeMessage> emails = new HashSet<>();
        for (Employee recipient : recipients) {
            emails.add(approvalEmail.createEmail(new TravelAppEmailView(event.getApplicationReview()), recipient));
        }
        sendMailService.sendMessages(emails);
    }

    /**
     * Sends an email to users when their application is disapproved.
     */
    @Subscribe
    public void sendDisapprovalEmails(TravelDisapprovalEmailEvent event) {
        Set<Employee> recipients = emailRecipients.forStatusUpdate(event.getAppReview().application());
        Set<MimeMessage> emails = new HashSet<>();
        for (Employee recipient : recipients) {
            TravelAppEmailView view = new TravelAppEmailView(event.getAppReview());
            emails.add(disapprovalEmail.createEmail(view, recipient));

        }
        sendMailService.sendMessages(emails);
    }

    public void sendEditEmails(TravelAppEditedEmailEvent event) {
        Set<Employee> recipients = emailRecipients.forStatusUpdate(event.getApplication());
        Set<MimeMessage> emails = new HashSet<>();
        for (Employee recipient : recipients) {
            TravelAppEmailView view = new TravelAppEmailView(event.getApplication());
            emails.add(editEmail.createEmail(view, recipient));

        }
        sendMailService.sendMessages(emails);
    }

    /**
     * Sends emails to reviewers when they have a new application to review.
     */
    @Subscribe
    public void sendPendingReviewEmail(TravelPendingReviewEmailEvent event) {
        Set<Employee> recipients = emailRecipients.forPendingReview(event.getAppReview());
        Set<MimeMessage> emails = new HashSet<>();
        for (Employee recipient : recipients) {
            TravelAppEmailView view = new TravelAppEmailView(event.getAppReview());
            emails.add(pendingAppReviewEmail.createEmail(view, recipient));

        }
        sendMailService.sendMessages(emails);
    }
}
