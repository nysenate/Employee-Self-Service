package gov.nysenate.ess.core.service.notification.email.simple.componet;

import gov.nysenate.ess.core.service.notification.base.message.base.Message;
import gov.nysenate.ess.core.service.notification.base.message.componet.UTF8PaintText;

import java.awt.*;

/**
 * Created by senateuser on 6/15/2016.
 */
public class SimpleEmailContent extends UTF8PaintText {

    public StringBuilder path = new StringBuilder(super.path).append(".").append(SimpleEmailContent.class.getSimpleName());
    private Color color;
    private String content;

    public SimpleEmailContent(Color color, String content) {
        super(color, content);
        this.color = color;
        this.content = content;
    }

    @Override
    public void attachTo(Message message) {
        message.setComponet(new SimpleEmailSubject(color, content));
    }
}
