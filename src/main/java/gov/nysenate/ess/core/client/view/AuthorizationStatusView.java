package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.model.auth.AuthorizationStatus;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AuthorizationStatusView
{
    protected boolean authorized = false;
    protected String code;
    protected String message;

    public AuthorizationStatusView(AuthorizationStatus authorizationStatus) {
        if (authorizationStatus != null) {
            this.authorized = authorizationStatus.isAuthorized();
            this.code = authorizationStatus.name();
            this.message = authorizationStatus.getMessage();
        }
    }

    @XmlElement
    public boolean isAuthorized() {
        return authorized;
    }

    @XmlElement
    public String getCode() {
        return code;
    }

    @XmlElement
    public String getMessage() {
        return message;
    }
}