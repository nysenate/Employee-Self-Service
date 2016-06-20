package gov.nysenate.ess.core.service.notification.email.simple.user;

import gov.nysenate.ess.core.model.personnel.Person;
import gov.nysenate.ess.core.service.notification.base.header.userType.Recevicer;

/**
 * Created by senateuser on 6/15/2016.
 */
public class SimpleEmailReceiver implements Recevicer {
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
