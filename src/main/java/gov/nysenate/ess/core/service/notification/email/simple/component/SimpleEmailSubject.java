package gov.nysenate.ess.core.service.notification.email.simple.component;

import gov.nysenate.ess.core.service.notification.base.message.component.UTF8PaintText;

/**
 *  Simple email subject
 * Created by Chenguang He on 6/14/2016.
 */
public class SimpleEmailSubject extends UTF8PaintText {
    public SimpleEmailSubject(String content) {
        super(content);
    }

    @Override
    public String getBind() {
        throw new UnsupportedOperationException("subject has no bind");
    }

}
