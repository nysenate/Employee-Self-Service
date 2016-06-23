package gov.nysenate.ess.core.service.notification.email.simple.componet;

import gov.nysenate.ess.core.service.notification.base.message.base.Message;
import gov.nysenate.ess.core.service.notification.base.message.componet.UTF8PaintText;

import java.awt.*;

/**
 *  Simple email subject
 * Created by Chenguang He on 6/14/2016.
 */
public class SimpleEmailSubject extends UTF8PaintText {

    public StringBuilder path = new StringBuilder(super.path).append("." + SimpleEmailSubject.class.getSimpleName());

    private Color color;
    private String content;

    /**
     * the constructor
     *
     * @param color   color
     * @param content content
     */
    public SimpleEmailSubject(Color color, String content) {
        super(color, content);
        this.color = color;
        this.content = content;
    }

    @Override
    public void attachTo(Message message) {
        message.setComponent(new SimpleEmailSubject(color, content));
    }

    @Override
    public String getBind() {
        throw new UnsupportedOperationException("subject has no bind");
    }

}
