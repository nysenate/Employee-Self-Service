package gov.nysenate.ess.web.security.exception;

import org.apache.shiro.authc.AuthenticationException;

public class NameNotFoundException extends AuthenticationException {

    public NameNotFoundException() {
    }

    public NameNotFoundException(String message) {
        super(message);
    }

    public NameNotFoundException(Throwable cause) {
        super(cause);
    }

    public NameNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
