package gov.nysenate.ess.core.client.response.auth;

import gov.nysenate.ess.core.model.auth.AuthenticationStatus;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AuthenticationResponse
{
    protected AuthenticationStatus status;
    protected String user;
    protected int employeeId;
    protected String redirectUrl;

    public AuthenticationResponse(AuthenticationStatus status, String user, int employeeId, String redirectUrl) {
        this.status = status;
        this.user = user;
        this.employeeId = employeeId;
        this.redirectUrl = redirectUrl;
    }

    public boolean isAuthenticated() {
        return status.isAuthenticated();
    }

    public String getStatus() {
        return status.name();
    }

    public String getMessage() {
        return status.getStatusMessage();
    }

    public String getUser() {
        return user;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
