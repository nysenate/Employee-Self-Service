package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.model.auth.AuthorizationStatus;
import gov.nysenate.ess.core.client.response.auth.AuthorizationResponse;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionApiCtrl extends BaseRestApiCtrl
{
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthenticatedException.class)
    public @ResponseBody
    AuthorizationResponse handleUnauthenticatedException(UnauthenticatedException ex) {
        Subject subject = getSubject();
        return new AuthorizationResponse(AuthorizationStatus.UNAUTHENTICATED, subject);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public @ResponseBody
    AuthorizationResponse handleUnauthorizedException(UnauthorizedException ex) {
        Subject subject = getSubject();
        return new AuthorizationResponse(AuthorizationStatus.UNAUTHORIZED, subject);
    }
}
