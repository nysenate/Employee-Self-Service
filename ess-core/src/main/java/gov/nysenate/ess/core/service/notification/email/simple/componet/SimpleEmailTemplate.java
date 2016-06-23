package gov.nysenate.ess.core.service.notification.email.simple.componet;

import gov.nysenate.ess.core.service.notification.base.message.base.Message;
import gov.nysenate.ess.core.service.notification.base.message.componet.UTF8PaintText;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

/**
 * Simple email use this class to build up its message by attaching the components into template
 * <p>
 * Created by Chenguang He on 6/21/2016.
 */
public class SimpleEmailTemplate extends UTF8PaintText {

    public StringBuilder path = new StringBuilder(super.path).append(".").append(SimpleEmailContent.class.getSimpleName());
    private Color color;
    private String content;
    private String templateFileName;

    /**
     * the constructor
     *
     * @param color            color of text
     * @param content          content of text
     * @param templateFileName name of template file
     * @throws IOException throw exception if the template file can not find.
     */
    public SimpleEmailTemplate(Color color, String content, String templateFileName) throws IOException {
        super(color, content);
        this.templateFileName = templateFileName;
        this.color = color;
        InputStream in = getClass().getClassLoader().getResourceAsStream("email.template/" + templateFileName);
        StringWriter writer = new StringWriter();
        IOUtils.copy(in, writer, Charset.defaultCharset()); // load file
        this.content = writer.toString();
    }

    @Override
    public void attachTo(Message message) throws IOException {
        message.setComponent(new SimpleEmailTemplate(color, content, templateFileName));
    }

    public String getContent() {
        return content;
    }

    @Override
    public String getBind() {
        throw new UnsupportedOperationException("Template do not have bind method");
    }
}
