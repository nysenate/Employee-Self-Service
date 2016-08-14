package gov.nysenate.ess.core.service.notification.email.simple.user;

import gov.nysenate.ess.core.model.personnel.Person;
import gov.nysenate.ess.core.service.notification.base.header.userType.Sender;

/**
 *  Simple email sender
 * Created by Chenguang He on 6/14/2016.
 */
public class SimpleEmailSender implements Sender {
    private Person person;

    private SimpleEmailSender() {
    }

    public SimpleEmailSender(Person person) {
        this.person = person;
    }

    public String getEmail() {
        return person.getEmail();
    }
}
