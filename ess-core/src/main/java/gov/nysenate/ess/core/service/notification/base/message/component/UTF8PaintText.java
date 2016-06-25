package gov.nysenate.ess.core.service.notification.base.message.component;

import gov.nysenate.ess.core.service.notification.base.message.base.Text;

import java.awt.*;
import java.nio.charset.Charset;

/**
 * UTF8 encoded text
 * Created by Chenguang He on 6/14/2016.
 */
public abstract class UTF8PaintText implements Text {

    public StringBuilder path = new StringBuilder(Text.path).append("." + UTF8PaintText.class.getSimpleName());

    private String content = new String();
    private Color color = new Color(0, 0, 0);// black
    private Integer id;

    private UTF8PaintText() {
    }

    /**
     * the constructor
     *
     * @param color   the color
     * @param content the content
     */
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
    public String getContent() {
        return content;
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
