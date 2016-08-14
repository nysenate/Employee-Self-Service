package gov.nysenate.ess.core.service.notification.base.message.base;

import java.awt.*;

/**
 * Created by Chenguang He on 6/14/2016.
 */
public interface Text extends Component {

    StringBuilder path = new StringBuilder(Component.path).append("." + Text.class.getSimpleName());

    /**
     * the encoding of text
     *
     * @return the name of encoding
     */
    String getEncoding();

    /**
     * get the content of text
     * @return the content
     */
    String getContent();

    /**
     * get the bind string of text
     *
     * @return the bind string
     */
    String getBind();

    /**
     * get the color of text
     * @return the color
     */
    Color getColor();

    /**
     * to string
     * @return string
     */
    String toString();
}
