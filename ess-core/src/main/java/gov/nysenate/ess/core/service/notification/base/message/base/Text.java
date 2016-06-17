package gov.nysenate.ess.core.service.notification.base.message.base;

import java.awt.*;

/**
 * Created by senateuser on 6/14/2016.
 */
public interface Text extends Componet {

    StringBuilder path = new StringBuilder(Componet.path).append("." + Text.class.getSimpleName());

    String getEncoding();

    void setEncoding(String encoding);

    String getContent();

    void setContent(String s);

    Color getColor();

    void setColor(Color color);

    String toString();
}
