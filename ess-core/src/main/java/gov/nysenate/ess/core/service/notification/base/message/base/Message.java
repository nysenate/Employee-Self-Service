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
     * deep copy of message
     * @return message
     */
    Message copyTo();

    /**
     * copy from message
     * @param message message
     */
    void copyFrom(Message message);

    /**
     * get id of message
     * @return message id
     */
    int getMessageId();

    /**
     * set id of message
     * @param id message id
     */
    void setMessageId(int id);

    /**
     * return true if the message has been built successfully
     * @return return true if the message has been built successfully
     */
    boolean isFalut();

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

    /**
     *  remove a header
     * @param name theader
     */
    void removeHeader(String name);

    String toString();

    int hashCode();

    boolean equals(Object o);
}
