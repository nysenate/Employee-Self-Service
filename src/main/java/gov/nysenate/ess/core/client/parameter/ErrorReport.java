package gov.nysenate.ess.core.client.parameter;

/**
 * Specifies the format for a user error report
 */
public final class ErrorReport {

    private Integer user;
    private String url;
    private Object details;

    private ErrorReport() {}

    public Integer getUser() {
        return user;
    }

    public String getUrl() {
        return url;
    }

    public Object getDetails() {
        return details;
    }
}
