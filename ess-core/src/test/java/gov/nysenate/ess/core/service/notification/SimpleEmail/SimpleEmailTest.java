package gov.nysenate.ess.core.service.notification.SimpleEmail;

import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.CoreTests;
import gov.nysenate.ess.core.model.personnel.Person;
import gov.nysenate.ess.core.service.notification.base.message.base.Componet;
import gov.nysenate.ess.core.service.notification.email.simple.componet.SimpleEmailContent;
import gov.nysenate.ess.core.service.notification.email.simple.componet.SimpleEmailSubject;
import gov.nysenate.ess.core.service.notification.email.simple.header.SimpleEmailHeader;
import gov.nysenate.ess.core.service.notification.email.simple.message.SimpleEmailHandler;
import gov.nysenate.ess.core.service.notification.email.simple.message.SimpleEmailMessage;
import gov.nysenate.ess.core.service.notification.email.simple.user.SimpleEmailRecevicer;
import gov.nysenate.ess.core.service.notification.email.simple.user.SimpleEmailSender;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chenguang He on 6/15/2016.
 */

public class SimpleEmailTest extends CoreTests {
    private static final Logger logger = LoggerFactory.getLogger(SimpleEmailTest.class);
    @Autowired
    EventBus eventBus;
    @Autowired
    SimpleEmailHandler simpleEmailHandler;

    @Test
    public void SimpleEmailTest() {
        /*
            email content
         */
        SimpleEmailSubject simpleEmailSubject = new SimpleEmailSubject(Color.black, "hello");
        SimpleEmailContent simpleEmailContent = new SimpleEmailContent(Color.black, "test content");
        List<Componet> componetList = new ArrayList<>();
        componetList.add(simpleEmailContent);
        componetList.add(simpleEmailSubject);
        /*
        Sender/Receiver
         */
        Person he = new Person();
        he.setEmail("gaoyike@gmail.com");
        Person she = new Person();
        she.setEmail("gaoyike@gmail.com");
        SimpleEmailSender simpleEmailSender = new SimpleEmailSender(he);
        SimpleEmailRecevicer simpleEmailRecevicer = new SimpleEmailRecevicer(she);

        /**
         * header
         */
        SimpleEmailHeader simpleEmailHeader = new SimpleEmailHeader();
        /**
         * compose email
         */
        SimpleEmailMessage simpleEmailMessage = new SimpleEmailMessage(simpleEmailSender, simpleEmailRecevicer, componetList, simpleEmailHeader.toMap(), 1);
        simpleEmailMessage.setComponet(simpleEmailContent);
        simpleEmailMessage.setComponet(simpleEmailSubject);

        eventBus.register(simpleEmailHandler);

        eventBus.post(simpleEmailMessage);
    }
}
