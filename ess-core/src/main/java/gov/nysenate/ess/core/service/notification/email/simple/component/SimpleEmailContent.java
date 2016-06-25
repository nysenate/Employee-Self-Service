package gov.nysenate.ess.core.service.notification.email.simple.component;

import gov.nysenate.ess.core.service.notification.base.message.base.Message;
import gov.nysenate.ess.core.service.notification.base.message.component.UTF8PaintText;

import java.awt.*;

/**
 *  Simple email content
 * Created by Chenguang He on 6/14/2016.
 */
public class SimpleEmailContent extends UTF8PaintText {

    public StringBuilder path = new StringBuilder(super.path).append(".").append(SimpleEmailContent.class.getSimpleName());
    private Color color;
    private String content;
    private String bindTo;

    /**
     * the constructor
     *
     * @param color   the color
     * @param content the content
     * @param bindTo  the bind text
     */
    public SimpleEmailContent(Color color, String content, String bindTo) {
        super(color, content);
        this.color = color;
        this.content = content;
        this.bindTo = bindTo;
    }

    @Override
    public void attachTo(Message message) {
        message.setComponent(new SimpleEmailSubject(color, content));
    }

    @Override
    public String getBind() {
        return bindTo;
    }

}
