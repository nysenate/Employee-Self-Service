package gov.nysenate.ess.travel.notifications.email;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.department.DepartmentDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.travel.review.ApplicationReview;
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
    private PendingAppReviewEmail pendingAppReviewEmail;
    private TravelEmailRecipients emailRecipients;

    @Autowired
    public TravelEmailService(SendMailService sendMailService,
                              TravelAppDisapprovalEmail disapprovalEmail,
                              TravelAppApprovalEmail approvalEmail,
                              PendingAppReviewEmail pendingAppReviewEmail,
                              TravelEmailRecipients emailRecipients) {
        this.sendMailService = sendMailService;
        this.disapprovalEmail = disapprovalEmail;
        this.approvalEmail = approvalEmail;
        this.pendingAppReviewEmail = pendingAppReviewEmail;
        this.emailRecipients = emailRecipients;
    }

    /**
     * Sends an email to users when their application is approved.
     * @param appReview
     */
    public void sendApprovalEmails(ApplicationReview appReview) {
        Set<Employee> recipients = emailRecipients.forStatusUpdate(appReview);
        Set<MimeMessage> emails = new HashSet<>();
        for (Employee recipient : recipients) {
            emails.add(approvalEmail.createEmail(new TravelAppEmailView(appReview), recipient));
        }
        sendMailService.sendMessages(emails);
    }

    /**
     * Sends an email to users when their application is disapproved.
     * @param appReview
     */
    public void sendDisapprovalEmails(ApplicationReview appReview) {
        Set<Employee> recipients = emailRecipients.forStatusUpdate(appReview);
        Set<MimeMessage> emails = new HashSet<>();
        for (Employee recipient : recipients) {
            TravelAppEmailView view = new TravelAppEmailView(appReview);
            emails.add(disapprovalEmail.createEmail(view, recipient));

        }
        sendMailService.sendMessages(emails);
    }

    /**
     * Sends emails to reviewers when they have a new application to review.
     * @param appReview
     */
    public void sendPendingReviewEmail(ApplicationReview appReview) {
        Set<Employee> recipients = emailRecipients.forPendingReview(appReview);
        Set<MimeMessage> emails = new HashSet<>();
        for (Employee recipient : recipients) {
            TravelAppEmailView view = new TravelAppEmailView(appReview);
            emails.add(pendingAppReviewEmail.createEmail(view, recipient));

        }
        sendMailService.sendMessages(emails);
    }
}
