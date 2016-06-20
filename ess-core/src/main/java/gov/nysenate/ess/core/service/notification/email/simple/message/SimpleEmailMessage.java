package gov.nysenate.ess.core.service.notification.email.simple.message;

import gov.nysenate.ess.core.service.notification.base.header.base.Header;
import gov.nysenate.ess.core.service.notification.base.message.base.Component;
import gov.nysenate.ess.core.service.notification.base.message.base.Message;
import gov.nysenate.ess.core.service.notification.email.simple.user.SimpleEmailReceiver;
import gov.nysenate.ess.core.service.notification.email.simple.user.SimpleEmailSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by senateuser on 6/14/2016.
 */
public class SimpleEmailMessage implements Message {
    private List<Component> comp;
    private Map<String, String> header;
    private Integer id;
    private SimpleEmailReceiver recevicer;
    private SimpleEmailSender sender;
    private String subject;

    private SimpleEmailMessage() {
    }

    public SimpleEmailMessage(SimpleEmailSender sender, SimpleEmailReceiver recevicer, List<Component> comp, Map<String, String> header, Integer id) {
        comp = new ArrayList<>();
        header = new HashMap<>();
        this.id = id;
        this.header = header;
        this.comp = comp;
        this.sender = sender;
        this.recevicer = recevicer;
    }

    @Override
    public SimpleEmailSender getSender() {
        return sender;
    }

    @Override
    public SimpleEmailReceiver getRecevicer() {
        return recevicer;
    }

    @Override
    public List<Component> getComponet() {
        return comp;
    }

    @Override
    public void setComponet(List<Component> components) {
        for (Component c : components) {
            setComponet(c);
        }
    }

    @Override
    public void setComponet(Component componets) {
        comp.add(componets);
    }

    @Override
    public Message copyTo() {
        Message clone = new SimpleEmailMessage(sender, recevicer, comp, header, id);
        return clone;
    }

    @Override
    public void copyFrom(Message message) {
        this.id = message.getMessageId();
        this.comp = message.getComponet();
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
