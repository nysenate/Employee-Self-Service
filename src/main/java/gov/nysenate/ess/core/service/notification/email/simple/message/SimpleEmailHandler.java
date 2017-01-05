package gov.nysenate.ess.core.service.notification.email.simple.message;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.ess.core.service.notification.base.handler.exception.UnsupportedMessageException;
import gov.nysenate.ess.core.service.notification.email.simple.service.SimpleEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Simple email handle deliver the message to it's service.
 * Created by Chenguang He on 6/14/2016.
 */
@Service
public class SimpleEmailHandler {

    @Autowired EventBus eventBus;
    @Autowired SimpleEmailService simpleEmailService;

    @PostConstruct
    public void init() {
        eventBus.register(this);
    }

    @Subscribe
    public void handle(SimpleEmailMessage message) throws UnsupportedMessageException, ClassNotFoundException {
        simpleEmailService.delivery(message);
    }
}
