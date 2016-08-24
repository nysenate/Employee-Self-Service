package gov.nysenate.ess.supply.notification;

import gov.nysenate.ess.core.service.notification.base.message.base.*;
import gov.nysenate.ess.core.service.notification.base.message.base.Component;
import gov.nysenate.ess.core.service.notification.email.simple.component.SimpleEmailContent;
import gov.nysenate.ess.core.service.notification.email.simple.component.SimpleEmailSubject;
import gov.nysenate.ess.core.service.notification.email.simple.component.SimpleEmailTemplate;
import gov.nysenate.ess.core.service.notification.email.simple.header.SimpleEmailHeader;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.util.mail.SendSimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

@Service
public class SupplyEmailService {

    private static final Logger logger = LoggerFactory.getLogger(SupplyEmailService.class);
    @Autowired private SendSimpleEmail sendSimpleEmail;

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
}
