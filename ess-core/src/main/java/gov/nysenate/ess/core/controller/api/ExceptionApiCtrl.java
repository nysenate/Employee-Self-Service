package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ErrorResponse;
import gov.nysenate.ess.core.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.ess.core.client.view.base.InvalidParameterView;
import gov.nysenate.ess.core.client.view.base.ParameterView;
import gov.nysenate.ess.core.model.auth.AuthorizationStatus;
import gov.nysenate.ess.core.client.response.auth.AuthorizationResponse;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This class contains global controller exception handlers.
 * Any exceptions thrown from a spring annotated controller that is not
 * handled in that controller will be handled here.
 */
@ControllerAdvice
public class ExceptionApiCtrl extends BaseRestApiCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(ExceptionApiCtrl.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    protected ErrorResponse handleUnknownError(Exception ex) {
        logger.error("Caught unhandled servlet exception:\n{}", ExceptionUtils.getStackTrace(ex));
        return new ErrorResponse(ErrorCode.APPLICATION_ERROR);
    }

    @ExceptionHandler(InvalidRequestParamEx.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected ErrorResponse handleInvalidRequestParameterException(InvalidRequestParamEx ex) {
        logger.debug("Handling invalid request param");
        logger.debug(ExceptionUtils.getStackTrace(ex));
        return new ViewObjectErrorResponse(ErrorCode.INVALID_ARGUMENTS, new InvalidParameterView(ex));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected ErrorResponse handleMissingParameterException(MissingServletRequestParameterException ex) {
        logger.debug("Handling missing servlet request param");
        logger.debug(ExceptionUtils.getStackTrace(ex));
        return new ViewObjectErrorResponse(ErrorCode.MISSING_PARAMETERS,
                                           new ParameterView(ex.getParameterName(), ex.getParameterType()));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthenticatedException.class)
    @ResponseBody
    public AuthorizationResponse handleUnauthenticatedException(UnauthenticatedException ex) {
        logger.debug(ExceptionUtils.getStackTrace(ex));
        Subject subject = getSubject();
        return new AuthorizationResponse(AuthorizationStatus.UNAUTHENTICATED, subject);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    public AuthorizationResponse handleUnauthorizedException(UnauthorizedException ex) {
        logger.debug(ExceptionUtils.getStackTrace(ex));
        Subject subject = getSubject();
        return new AuthorizationResponse(AuthorizationStatus.UNAUTHORIZED, subject);
    }
}
