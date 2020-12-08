package gov.nysenate.ess.travel.notifications.email;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.travel.application.TravelApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Collections;

@Service
public class TravelEmailService {

    private SendMailService sendMailService;
    private TravelAppDisapprovalEmail disapprovalEmail;
    private TravelAppApprovalEmail approvalEmail;

    @Autowired
    public TravelEmailService(SendMailService sendMailService,
                              TravelAppDisapprovalEmail disapprovalEmail,
                              TravelAppApprovalEmail approvalEmail) {
        this.sendMailService = sendMailService;
        this.disapprovalEmail = disapprovalEmail;
        this.approvalEmail = approvalEmail;
    }

    public void sendApprovalEmails(TravelApplication app) {
        MimeMessage email = approvalEmail.createEmail(app, app.getSubmittedBy());
        sendMailService.sendMessages(Collections.singleton(email));
    }

    public void sendDisapprovalEmails(TravelApplication app, Employee disapprover, String reason) {
        MimeMessage email = disapprovalEmail.createEmail(app, app.getSubmittedBy(), disapprover, reason);
        sendMailService.sendMessages(Collections.singleton(email));
    }
}
