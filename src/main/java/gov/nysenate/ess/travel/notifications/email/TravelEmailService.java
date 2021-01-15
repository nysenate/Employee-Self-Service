package gov.nysenate.ess.travel.notifications.email;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.review.ApplicationReview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TravelEmailService {

    private SendMailService sendMailService;
    private TravelAppDisapprovalEmail disapprovalEmail;
    private TravelAppApprovalEmail approvalEmail;
    private PendingAppReviewEmail pendingAppReviewEmail;

    @Autowired
    public TravelEmailService(SendMailService sendMailService,
                              TravelAppDisapprovalEmail disapprovalEmail,
                              TravelAppApprovalEmail approvalEmail,
                              PendingAppReviewEmail pendingAppReviewEmail) {
        this.sendMailService = sendMailService;
        this.disapprovalEmail = disapprovalEmail;
        this.approvalEmail = approvalEmail;
        this.pendingAppReviewEmail = pendingAppReviewEmail;
    }

    public void sendApprovalEmails(ApplicationReview appReview) {
        Set<Employee> recipients = Sets.newHashSet(
                appReview.application().getSubmittedBy(),
                appReview.application().getTraveler());
        Set<MimeMessage> emails = new HashSet<>();

        for (Employee recipient : recipients) {
            emails.add(approvalEmail.createEmail(new TravelAppEmailView(appReview), recipient));
        }
        sendMailService.sendMessages(emails);
    }

    public void sendDisapprovalEmails(ApplicationReview appReview) {
        Set<Employee> recipients = Sets.newHashSet(
                appReview.application().getSubmittedBy(),
                appReview.application().getTraveler());
        Set<MimeMessage> emails = new HashSet<>();

        for (Employee recipient : recipients) {
            TravelAppEmailView view = new TravelAppEmailView(appReview);
            emails.add(disapprovalEmail.createEmail(view, recipient));

        }
        sendMailService.sendMessages(emails);
    }

    public void sendPendingReviewEmail(ApplicationReview appReview) {
        // TODO This is just dummy data for testing
        Set<Employee> recipients = Sets.newHashSet(
                appReview.application().getSubmittedBy());
        Set<MimeMessage> emails = new HashSet<>();

        for (Employee recipient : recipients) {
            TravelAppEmailView view = new TravelAppEmailView(appReview);
            emails.add(pendingAppReviewEmail.createEmail(view, recipient));

        }
        sendMailService.sendMessages(emails);
    }
}
