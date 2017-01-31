package gov.nysenate.ess.core.service.template;

public class EssTemplateException extends RuntimeException {

    private static final long serialVersionUID = -3377261242687173306L;

    public EssTemplateException(String templateName, Throwable cause) {
        super("An error occurred while attempting to process ess template: " + templateName, cause);
    }
}
