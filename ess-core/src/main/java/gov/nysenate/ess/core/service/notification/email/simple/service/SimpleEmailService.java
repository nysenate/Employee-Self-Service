package gov.nysenate.ess.core.service.notification.email.simple.service;

import gov.nysenate.ess.core.service.mail.MimeSendMailService;
import gov.nysenate.ess.core.service.notification.NotificationUtils;
import gov.nysenate.ess.core.service.notification.base.handler.base.DeliverServices;
import gov.nysenate.ess.core.service.notification.base.message.base.Componet;
import gov.nysenate.ess.core.service.notification.email.simple.componet.SimpleEmailContent;
import gov.nysenate.ess.core.service.notification.email.simple.componet.SimpleEmailSubject;
import gov.nysenate.ess.core.service.notification.email.simple.message.SimpleEmailMessage;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by Chenguang He on 6/16/2016.
 */

@Service
public class SimpleEmailService extends MimeSendMailService implements DeliverServices<SimpleEmailMessage> {
    @Override
    public void delivery(SimpleEmailMessage message) throws ClassNotFoundException {
        List<Componet> list = message.getComponet();
        String subject = "";
        String content = "";
        for (int i = 0; i < list.size(); i++) {
            Object o = NotificationUtils.deCompoent(list.get(i).getClass(), list.get(i));
            if (o instanceof SimpleEmailContent) {
                content = ((SimpleEmailContent) o).getContent();
            }
            if (o instanceof SimpleEmailSubject) {
                subject = ((SimpleEmailSubject) o).getContent();
            }
        }
        sendMessage(message.getRecevicer().getEmail(), message.getSender().getEmail(), subject, content);
    }
}
