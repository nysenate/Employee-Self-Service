package gov.nysenate.ess.core.service.notification.email.simple.message;

import gov.nysenate.ess.core.service.notification.base.handler.base.Handler;
import gov.nysenate.ess.core.service.notification.base.handler.exception.UnSupportMessageException;
import gov.nysenate.ess.core.service.notification.email.simple.service.SimpleEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by senateuser on 6/15/2016.
 */
@Service
public class SimpleEmailHandler implements Handler<SimpleEmailMessage> {
    @Autowired
    SimpleEmailService simpleEmailService;

    @Override
    public void handle(SimpleEmailMessage message) throws UnSupportMessageException, ClassNotFoundException {
        simpleEmailService.delivery(message);
    }
}
