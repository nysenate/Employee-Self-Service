package gov.nysenate.ess.core.service.notification.base.message.component;

import gov.nysenate.ess.core.service.notification.base.message.base.Message;
import gov.nysenate.ess.core.service.notification.base.message.base.Text;

import java.awt.*;

/**
 *  the hyper link class in email message
 * Created by Chenguang He  on 6/15/2016.
 */
public class Hyperlink implements Text {

    private StringBuilder path = Text.path.append("." + Hyperlink.class.getName());

    private String encode = "ASCII"; // encode
    private String url;
    private String content;
    private Color color = new Color(0, 0, 0);
    private Integer id;
    private String bindTo;

    private Hyperlink() {
    }

    /**
     * the constructor
     *
     * @param url     the url
     * @param content the content
     * @param color   the color
     * @param bindTo  the bind to
     */
    public Hyperlink(String url, String content, Color color, String bindTo) {
        this.url = url;
        this.content = content;
        this.color = color;
        this.bindTo  = bindTo;
    }

    @Override
    public String getEncoding() {
        return encode;
    }


    @Override
    public String getContent() {
        return "<a href=" + url + ">" + content + "</a>";
    }

    @Override
    public String getBind() {
        return bindTo;
    }


    @Override
    public Color getColor() {
        return color;
    }


    @Override
    public int getComponetId() {
        return id;
    }

    @Override
    public void attachTo(Message message) {
        message.setComponent(new Hyperlink(url, content, color,bindTo));
    }


}
