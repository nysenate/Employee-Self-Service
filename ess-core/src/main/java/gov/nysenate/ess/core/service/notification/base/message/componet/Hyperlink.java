package gov.nysenate.ess.core.service.notification.base.message.componet;

import gov.nysenate.ess.core.service.notification.base.message.base.Text;

import java.awt.*;

/**
 * Created by senateuser on 6/14/2016.
 */
public abstract class Hyperlink implements Text {

    private StringBuilder path = Text.path.append("." + Hyperlink.class.getName());

    private String encode = "ASCII";
    private String url;
    private String content;
    private Color color = new Color(0, 0, 0);
    private Integer id;

    private Hyperlink() {
    }

    public Hyperlink(String url, String content, Color color) {
        this.url = url;
        this.content = content;
        this.color = color;
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
    public Color getColor() {
        return color;
    }


    @Override
    public int getComponetId() {
        return id;
    }


}
