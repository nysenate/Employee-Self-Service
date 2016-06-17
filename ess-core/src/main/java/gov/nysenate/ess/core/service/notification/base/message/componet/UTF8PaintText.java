package gov.nysenate.ess.core.service.notification.base.message.componet;

import gov.nysenate.ess.core.service.notification.base.message.base.Text;

import java.awt.*;
import java.nio.charset.Charset;

/**
 * Created by senateuser on 6/14/2016.
 */
public abstract class UTF8PaintText implements Text {

    public StringBuilder path = new StringBuilder(Text.path).append("." + UTF8PaintText.class.getSimpleName());

    private String content = new String();
    private Color color = new Color(0, 0, 0);// black
    private Integer id;

    private UTF8PaintText() {
    }

    public UTF8PaintText(Color color, String content) {
        this.color = color;
        this.content = content;
        Charset.forName("UTF-8").encode(content);
    }

    @Override
    public String getEncoding() {
        return "utf-8";
    }

    @Override
    @Deprecated
    public void setEncoding(String encoding) {
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String s) {
        content = s;
        Charset.forName("UTF-8").encode(content);
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
        this.id = Integer.valueOf(id);
    }

}
