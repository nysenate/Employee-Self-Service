package gov.nysenate.ess.core.service.notification.base.message.component;

import gov.nysenate.ess.core.service.notification.base.message.base.Text;

import java.nio.charset.StandardCharsets;

/**
 * UTF8 encoded text
 * Created by Chenguang He on 6/14/2016.
 */
public abstract class UTF8PaintText implements Text {
    private final String content;

    public UTF8PaintText(String content) {
        this.content = content;
        StandardCharsets.UTF_8.encode(content);
    }


    @Override
    public String getContent() {
        return content;
    }
}
