package gov.nysenate.ess.core.service.notification.email.simple.component;

import gov.nysenate.ess.core.service.notification.base.message.component.UTF8PaintText;

/**
 * Simple email use this class to build up its message by attaching the components into template
 * <p>
 * Created by Chenguang He on 6/21/2016.
 */
public class SimpleEmailTemplate extends UTF8PaintText {
    public SimpleEmailTemplate(String content) {
        super(content);
    }

    @Override
    public String getBind() {
        throw new UnsupportedOperationException("Template do not have bind method");
    }
}
