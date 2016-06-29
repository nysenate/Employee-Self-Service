package gov.nysenate.ess.core.service.notification.email.simple.message;

import gov.nysenate.ess.core.service.notification.base.handler.base.Handler;
import gov.nysenate.ess.core.service.notification.base.handler.exception.UnsupportedMessageException;
import gov.nysenate.ess.core.service.notification.email.simple.service.SimpleEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Simple email handle deliver the message to it's service.
 * Created by Chenguang He on 6/14/2016.
 */
@Service
public class SimpleEmailHandler implements Handler<SimpleEmailMessage> {
    @Autowired
    SimpleEmailService simpleEmailService;

    @Override
    public void handle(SimpleEmailMessage message) throws UnsupportedMessageException, ClassNotFoundException {
        simpleEmailService.delivery(message);
    }
}
