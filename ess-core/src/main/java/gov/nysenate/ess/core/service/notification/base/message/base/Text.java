package gov.nysenate.ess.core.service.notification.base.message.base;

import java.awt.*;

/**
 * Created by senateuser on 6/14/2016.
 */
public interface Text extends Component {

    StringBuilder path = new StringBuilder(Component.path).append("." + Text.class.getSimpleName());

    String getEncoding();

    String getContent();


    Color getColor();


    String toString();
}
