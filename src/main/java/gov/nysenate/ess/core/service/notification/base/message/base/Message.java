package gov.nysenate.ess.core.service.notification.base.message.base;

import gov.nysenate.ess.core.service.notification.base.header.base.Header;
import gov.nysenate.ess.core.service.notification.base.header.userType.Receiver;
import gov.nysenate.ess.core.service.notification.base.header.userType.Sender;

import java.util.List;
import java.util.Map;

/**
 *  message interface
 * Created by Chenguang He  on 6/15/2016.
 */
public interface Message {
    /**
     * get the sender
     *
     * @return sender
     */
    Sender getSender();

    /**
     * get the receiver
     *
     * @return the receiver
     */
    Receiver getReceiver();


    /**
     * get all  components
     *
     * @return all Component
     */
    List<Component> getComponent();

    /**
     * set Component
     *
     * @param components Components
     */
    void setComponent(List<Component> components);

    /**
     * set Component
     *
     * @param components Component
     */
    void setComponent(Component components);

    /**
     * set headers
     * @param headers headers
     */
    void setHeader(Header... headers);

    /**
     * get map of headers
     * @return the map
     */
    Map<String, String> getHeader();
}
