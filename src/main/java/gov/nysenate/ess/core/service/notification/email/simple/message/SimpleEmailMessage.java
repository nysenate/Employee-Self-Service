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
    private List<Component> comp;
    private Map<String, String> header;
    private Integer id;
    private SimpleEmailReceiver receiver;
    private SimpleEmailSender sender;
    private String subject;

    private SimpleEmailMessage() {
    }

    /**
     * the constructor
     *
     * @param sender   sender
     * @param receiver receiver
     * @param comp     the list of component
     * @param header   the headers
     * @param id       the id of message
     */
    public SimpleEmailMessage(SimpleEmailSender sender, SimpleEmailReceiver receiver, List<Component> comp, Map<String, String> header, Integer id) {
        this.id = id;
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
    public Message copyTo() {
        Message clone = new SimpleEmailMessage(sender, receiver, comp, header, id);
        return clone;
    }

    @Override
    public void copyFrom(Message message) {
        this.id = message.getMessageId();
        this.comp = message.getComponent();
        this.header = message.getHeader();
    }

    @Override
    public int getMessageId() {
        return id;
    }

    @Override
    public void setMessageId(int id) {
        this.id = id;
    }

    @Override
    public boolean isFalut() {
        return false;
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

    @Override
    public void removeHeader(String name) {
        header.remove(name);
    }
}
