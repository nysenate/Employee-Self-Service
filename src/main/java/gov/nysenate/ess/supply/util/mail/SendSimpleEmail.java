package gov.nysenate.ess.supply.util.mail;

import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.notification.base.message.base.Component;
import gov.nysenate.ess.core.service.notification.email.simple.component.SimpleEmailSubject;
import gov.nysenate.ess.core.service.notification.email.simple.header.SimpleEmailHeader;
import gov.nysenate.ess.core.service.notification.email.simple.message.SimpleEmailMessage;
import gov.nysenate.ess.core.service.notification.email.simple.user.SimpleEmailReceiver;
import gov.nysenate.ess.core.service.notification.email.simple.user.SimpleEmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chenguang He on 6/20/2016.
 */
@Service
public class SendSimpleEmail {
    @Autowired
    EventBus eventBus;

    private SendSimpleEmail() {
    }

    public void send(Employee sender, Employee receiver, List<Component> simpleEmailContent,
                     SimpleEmailHeader simpleEmailHeader, SimpleEmailSubject simpleEmailSubject, int messageId) {
        List<Component> components = new ArrayList<>(simpleEmailContent);
        components.add(simpleEmailSubject);

        SimpleEmailSender simpleEmailSender = new SimpleEmailSender(sender);
        SimpleEmailReceiver simpleEmailReceiver = new SimpleEmailReceiver(receiver);

        SimpleEmailMessage simpleEmailMessage = new SimpleEmailMessage(
                simpleEmailSender, simpleEmailReceiver, components, simpleEmailHeader.toMap(), messageId
        );
        eventBus.post(simpleEmailMessage);
    }
}
