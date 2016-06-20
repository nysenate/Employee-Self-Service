package gov.nysenate.ess.core.service.notification.base.message.base;

import gov.nysenate.ess.core.service.notification.base.header.base.Header;
import gov.nysenate.ess.core.service.notification.base.header.userType.Recevicer;
import gov.nysenate.ess.core.service.notification.base.header.userType.Sender;

import java.util.List;
import java.util.Map;

/**
 * Created by senateuser on 6/14/2016.
 */
public interface Message {
    Sender getSender();

    Recevicer getRecevicer();

    List<Component> getComponet();

    void setComponet(List<Component> components);

    void setComponet(Component componets);

    Message copyTo();

    void copyFrom(Message message);

    int getMessageId();

    void setMessageId(int id);

    boolean isFalut();

    void setHeader(Header... headers);

    Map<String, String> getHeader();

    void removeHeader(String name);

    String toString();

    int hashCode();

    boolean equals(Object o);
}
