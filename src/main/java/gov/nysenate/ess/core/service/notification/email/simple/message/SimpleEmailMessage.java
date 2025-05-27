package gov.nysenate.ess.core.service.notification.email.simple.message;

import gov.nysenate.ess.core.service.notification.base.header.base.Header;
import gov.nysenate.ess.core.service.notification.base.message.base.Component;
import gov.nysenate.ess.core.service.notification.base.message.base.Message;
import gov.nysenate.ess.core.service.notification.email.simple.user.SimpleEmailReceiver;
import gov.nysenate.ess.core.service.notification.email.simple.user.SimpleEmailSender;

import java.util.List;
import java.util.Map;

/**
 * Simple email message
 * Created by Chenguang He on 6/14/2016.
 */
public class SimpleEmailMessage implements Message {
    private final List<Component> comp;
    private final Map<String, String> header;
    private final SimpleEmailReceiver receiver;
    private final SimpleEmailSender sender;

    /**
     * the constructor
     *
     * @param sender   sender
     * @param receiver receiver
     * @param comp     the list of component
     * @param header   the headers
     */
    public SimpleEmailMessage(SimpleEmailSender sender, SimpleEmailReceiver receiver, List<Component> comp, Map<String, String> header) {
        this.header = header;
        this.comp = comp;
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public SimpleEmailSender getSender() {
        return sender;
    }

    @Override
    public SimpleEmailReceiver getReceiver() {
        return receiver;
    }

    @Override
    public List<Component> getComponent() {
        return comp;
    }

    @Override
    public void setComponent(List<Component> components) {
        for (Component c : components) {
            setComponent(c);
        }
    }

    @Override
    public void setComponent(Component components) {
        comp.add(components);
    }

    @Override
    public void setHeader(Header... headers) {
        for (Header h : headers)
            header.put(h.getHeaderName(), h.getHeaderValue());
    }

    @Override
    public Map<String, String> getHeader() {
        return header;
    }

}
