package gov.nysenate.ess.core.service.notification.base.message.base;

import java.io.IOException;

/**
 *  the component class use to represent a component in message
 * Created by Chenguang He on 6/14/2016.
 */
public interface Component {

    StringBuilder path = new StringBuilder(Component.class.getSimpleName());

    /**
     * the id of Component
     *
     * @return id
     */
    int getComponetId();

    /**
     * represent how this Component attach to an message
     *
     * @param message
     * @throws IOException
     */
    void attachTo(Message message) throws IOException;
}
