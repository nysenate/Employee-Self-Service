package gov.nysenate.ess.core.service.notification.email.simple.service;

import gov.nysenate.ess.core.service.mail.MimeSendMailService;
import gov.nysenate.ess.core.service.notification.NotificationUtils;
import gov.nysenate.ess.core.service.notification.base.handler.base.DeliverServices;
import gov.nysenate.ess.core.service.notification.base.message.base.Component;
import gov.nysenate.ess.core.service.notification.email.simple.component.SimpleEmailContent;
import gov.nysenate.ess.core.service.notification.email.simple.component.SimpleEmailSubject;
import gov.nysenate.ess.core.service.notification.email.simple.component.SimpleEmailTemplate;
import gov.nysenate.ess.core.service.notification.email.simple.message.SimpleEmailMessage;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *  Simple email service handles the message get from the handler
 * Created by Chenguang He on 6/16/2016.
 */

@Service
public class SimpleEmailService extends MimeSendMailService implements DeliverServices<SimpleEmailMessage> {
    @Override
    public void delivery(SimpleEmailMessage message) throws ClassNotFoundException {
        List<Component> list = message.getComponent();
        String subject = "";
        String content = new String();
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Object o = NotificationUtils.deCompoent(list.get(i).getClass(), list.get(i)); // get the object with its class
            if (o instanceof SimpleEmailTemplate) {
                SimpleEmailTemplate simpleEmailTemplate = ((SimpleEmailTemplate) o);
                content = simpleEmailTemplate.getContent();
            }
            if (o instanceof SimpleEmailContent) {
                SimpleEmailContent simpleEmailContent = ((SimpleEmailContent) o);
                map.put(simpleEmailContent.getBind(), simpleEmailContent.getContent());
            }
            if (o instanceof SimpleEmailSubject) {
                subject = ((SimpleEmailSubject) o).getContent();
            }
        }
        for (Map.Entry<String, String> e :
                map.entrySet()) {
            content = content.replace(e.getKey(), e.getValue());
        }
        sendMessage(message.getReceiver().getEmail(), message.getSender().getEmail(), subject, content);
    }
}
