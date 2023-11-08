package gov.nysenate.ess.core.service.notification.email.simple.component;

import gov.nysenate.ess.core.service.notification.base.message.component.UTF8PaintText;

/**
 *  Simple email content
 * Created by Chenguang He on 6/14/2016.
 */
public class SimpleEmailContent extends UTF8PaintText {
    private final String bindTo;

    /**
     * the constructor
     *
     * @param content the content
     * @param bindTo  the bind text
     */
    public SimpleEmailContent(String content, String bindTo) {
        super(content);
        this.bindTo = bindTo;
    }

    @Override
    public String getBind() {
        return bindTo;
    }
}
