package gov.nysenate.ess.core.service.template;

import freemarker.template.Template;

public class EssTemplateException extends RuntimeException {

    private static final long serialVersionUID = -3377261242687173306L;

    public EssTemplateException(String message, Throwable cause) {
        super(message, cause);
    }

    public EssTemplateException(Template template, Throwable cause) {
        this("An error occured while attempting to process ess template: " + template.getName(), cause);
    }
}
