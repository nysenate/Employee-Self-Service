package gov.nysenate.ess.core.service.notification.base.message.componet;

import gov.nysenate.ess.core.service.notification.base.message.base.Text;

import java.awt.*;

/**
 * Created by senateuser on 6/14/2016.
 */
public abstract class URL implements Text {

    private StringBuilder path = Text.path.append("." + URL.class.getName());

    private String encode = "ASCII";
    private String url;
    private Color color = new Color(0, 0, 0);
    private Integer id;

    private URL() {
    }

    public URL(String url, Color color) {
        this.url = url;
        this.color = color;
    }

    @Override
    public String getEncoding() {
        return encode;
    }

    @Override
    public void setEncoding(String encoding) {
        this.encode = encoding;
    }

    @Override
    public String getContent() {
        return url;
    }

    @Override
    public void setContent(String s) {
        url = s;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int getComponetId() {
        return id;
    }

    @Override
    public void setComponetId(int id) {
        this.id = id;
    }

}
