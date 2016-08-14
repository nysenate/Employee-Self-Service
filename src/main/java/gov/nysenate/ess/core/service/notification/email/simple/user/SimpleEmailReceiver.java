package gov.nysenate.ess.core.service.notification.email.simple.user;

import gov.nysenate.ess.core.model.personnel.Person;
import gov.nysenate.ess.core.service.notification.base.header.userType.Receiver;

/**
 *  Simple email receiver
 * Created by Chenguang He on 6/14/2016.
 */
public class SimpleEmailReceiver implements Receiver {
    private Person person;

    private SimpleEmailReceiver() {
    }

    public SimpleEmailReceiver(Person person) {
        this.person = person;
    }

    public String getEmail() {
        return person.getEmail();
    }
}
