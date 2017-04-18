package gov.nysenate.ess.supply.notification;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.notification.base.message.base.Component;
import gov.nysenate.ess.core.service.notification.email.simple.component.SimpleEmailContent;
import gov.nysenate.ess.core.service.notification.email.simple.component.SimpleEmailSubject;
import gov.nysenate.ess.core.service.notification.email.simple.component.SimpleEmailTemplate;
import gov.nysenate.ess.core.service.notification.email.simple.header.SimpleEmailHeader;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.util.mail.SendSimpleEmail;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupplyEmailService {

    private static final Logger logger = LoggerFactory.getLogger(SupplyEmailService.class);

    private SendSimpleEmail sendSimpleEmail;
    private SendMailService sendMailService;
    private CustomerConfirmationEmail confirmationEmail;
    private NewRequisitionEmail newRequisitionEmail;
    /** A collection of email addresses to notify of new requisition orders.*/
    private ImmutableSet<String> newReqEmailList;

    @Autowired
    public SupplyEmailService(SendSimpleEmail sendSimpleEmail, SendMailService sendMailService,
                              CustomerConfirmationEmail confirmationEmail, NewRequisitionEmail newRequisitionEmail,
                              @Value("${supply.requisition.notification.list:}") final String notificationList) {
        this.sendSimpleEmail = sendSimpleEmail;
        this.sendMailService = sendMailService;
        this.confirmationEmail = confirmationEmail;
        this.newRequisitionEmail = newRequisitionEmail;
        this.newReqEmailList = Arrays.stream(StringUtils.split(notificationList, ","))
                .map(StringUtils::trim)
                .collect(Collectors.collectingAndThen(Collectors.toSet(), ImmutableSet::copyOf));
    }

    @Async
    public void sendRejectEmail(Requisition requisition) {
        SimpleEmailSubject subject = new SimpleEmailSubject(Color.black, "Your supply requisition request (" + requisition.getRequisitionId() + ") has been rejected");
        // Elements
        SimpleEmailContent detail = new SimpleEmailContent(Color.black, requisition.toOrderString(), "$detail$");
        SimpleEmailContent note = new SimpleEmailContent(Color.black, requisition.getNote().get(), "$note$");
        SimpleEmailContent rId = new SimpleEmailContent(Color.black, String.valueOf(requisition.getRequisitionId()), "$requisitionId$");
        SimpleEmailContent cname = new SimpleEmailContent(Color.black, String.valueOf(requisition.getCustomer().getFirstName() + " " + requisition.getCustomer().getLastName()), "$cname$");
        SimpleEmailTemplate reject;
        try {
            reject = new SimpleEmailTemplate(Color.black, "", "reject_email");
            ArrayList<Component> simpleEmailContentList = new ArrayList<>();
            simpleEmailContentList.add(note);
            simpleEmailContentList.add(rId);
            simpleEmailContentList.add(cname);
            simpleEmailContentList.add(reject);
            simpleEmailContentList.add(detail);
            sendSimpleEmail.send(requisition.getIssuer().orElse(requisition.getModifiedBy()), requisition.getCustomer(),
                                 simpleEmailContentList, new SimpleEmailHeader(), subject, 1);
        } catch (IOException e) {
            logger.error("Error sending supply reject email", e);
        }
    }

    /**
     * Send all notifications for a new requisition order event.
     */
    @Async
    public void sendNewRequisitionNotifications(Requisition requisition) {
        sendRequisitionConfirmations(requisition);
        sendRequisitionNotifications(requisition);
    }

    private void sendRequisitionConfirmations(Requisition requisition) {
        String toEmail = requisition.getCustomer().getEmail();
        logger.info("Sending requisition confirmation email to: " + toEmail);
        MimeMessage confirmation = confirmationEmail.generateConfirmationEmail(requisition, toEmail);
        sendMailService.sendMessages(Collections.singleton(confirmation));
    }

    private void sendRequisitionNotifications(Requisition requisition) {
        logger.info("Sending requisition notification email to: " + String.join(", ", newReqEmailList));
        Set<MimeMessage> notificationMessages = newReqEmailList.stream()
                .map(e -> newRequisitionEmail.generateNewRequisitionEmail(requisition, e))
                .collect(Collectors.toSet());
        sendMailService.sendMessages(notificationMessages);
    }
}
