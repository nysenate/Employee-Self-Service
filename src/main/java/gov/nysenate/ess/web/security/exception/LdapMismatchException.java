package gov.nysenate.ess.web.security.exception;

import org.apache.shiro.authc.AuthenticationException;

public class LdapMismatchException extends AuthenticationException {

    public LdapMismatchException() {
    }

    public LdapMismatchException(String message) {
        super(message);
    }

    public LdapMismatchException(Throwable cause) {
        super(cause);
    }

    public LdapMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
