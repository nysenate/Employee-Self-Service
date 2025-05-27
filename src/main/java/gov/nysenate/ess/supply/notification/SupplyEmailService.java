package gov.nysenate.ess.supply.notification;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SupplyEmailService {

    private static final Logger logger = LoggerFactory.getLogger(SupplyEmailService.class);

    private SendMailService sendMailService;
    private CustomerConfirmationEmail confirmationEmail;
    private NewRequisitionEmail newRequisitionEmail;
    private RejectRequisitionEmail rejectRequisitionEmail;
    /** A collection of email addresses to notify of new requisition orders.*/
    private ImmutableSet<String> newReqEmailList;

    @Autowired
    public SupplyEmailService(SendMailService sendMailService, CustomerConfirmationEmail confirmationEmail,
                              NewRequisitionEmail newRequisitionEmail, RejectRequisitionEmail rejectRequisitionEmail,
                              @Value("${supply.requisition.notification.list:}") final String notificationList) {
        this.sendMailService = sendMailService;
        this.confirmationEmail = confirmationEmail;
        this.newRequisitionEmail = newRequisitionEmail;
        this.rejectRequisitionEmail = rejectRequisitionEmail;
        this.newReqEmailList = Arrays.stream(StringUtils.split(notificationList, ","))
                .map(StringUtils::trim)
                .collect(Collectors.collectingAndThen(Collectors.toSet(), ImmutableSet::copyOf));
    }

    /**
     * Send the user a notification email if their requisition gets rejected.
     * @param requisition The rejected requisition.
     */
    @Async
    public void sendRejectEmail(Requisition requisition) {
        String toEmail = requisition.getCustomer().getEmail();
        logger.info("Sending requisition rejection email to: " + toEmail);
        MimeMessage message = rejectRequisitionEmail.generateRejectionEmail(requisition, toEmail);
        sendMailService.sendMessages(Collections.singleton(message));
    }

    /**
     * Send all notifications for a new requisition order event.
     * @param requisition The new requisition.
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
