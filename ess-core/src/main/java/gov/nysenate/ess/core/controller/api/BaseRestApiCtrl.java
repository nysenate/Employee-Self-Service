package gov.nysenate.ess.core.controller.api;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.model.auth.DateTimeRangePermission;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.util.LimitOffset;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.function.Function;

public class BaseRestApiCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(BaseRestApiCtrl.class);

    public static final String REST_PATH = "/api/v1/";

    /** Maximum number of results that can be requested via the query params. */
    private static final int MAX_LIMIT = 1000;

    @Autowired protected EventBus eventBus;

    @PostConstruct
    public void init() {
        this.eventBus.register(this);
    }

    /** --- Request Parsers / Getters --- */

    /**
     * @return The currently authenticated subject
     */
    protected Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    /**
     * Check that the currently authenticated subject is authorized for the given permission at the given time
     * @param permission
     * @throws AuthorizationException if the user is not authorized for the given permission
     */
    protected void checkPermission(Permission permission)
            throws AuthorizationException {
        getSubject().checkPermission(permission);
    }


    /**
     * Attempts to parse a date request parameter
     * Throws an InvalidRequestParameterException if the parsing went wrong
     *
     * @param dateString The parameter value to be parsed
     * @param paramName The name of the parameter.  Used to generate the exception
     * @return LocalDate
     * @throws InvalidRequestParamEx
     */
    protected LocalDate parseISODate(String dateString, String paramName) {
        try {
            return LocalDate.from(DateTimeFormatter.ISO_DATE.parse(dateString));
        }
        catch (DateTimeParseException ex) {
            throw new InvalidRequestParamEx(dateString, paramName,
                    "date", "ISO 8601 date formatted string e.g. 2014-10-27 for October 27, 2014");
        }
    }

    /**
     * Attempts to parse a date time request parameter
     * Throws an InvalidRequestParameterException if the parsing went wrong
     *
     * @param dateTimeString The parameter value to be parsed
     * @param paramName The name of the parameter.  Used to generate the exception
     * @return LocalDateTime
     * @throws InvalidRequestParamEx
     */
    protected LocalDateTime parseISODateTime(String dateTimeString, String paramName) {
        try {
            return LocalDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(dateTimeString));
        }
        catch (DateTimeParseException | NullPointerException ex) {
            throw new InvalidRequestParamEx(dateTimeString, paramName,
                    "date-time", "ISO 8601 date and time formatted string e.g. 2014-10-27T09:44:55 for October 27, 2014 9:44:55 AM");
        }
    }

    /**
     * Constructs a Range from the given parameters.  Throws an exception if the parameter values are invalid
     * @param lower T
     * @param upper T
     * @param lowerName String
     * @param upperName String
     * @param lowerType BoundType
     * @param upperType BoundType
     * @param <T> T
     * @return Range<T>
     */
    protected <T extends Comparable> Range<T> getRange(T lower, T upper, String lowerName, String upperName,
                                                       BoundType lowerType, BoundType upperType) {
        try {
            return Range.range(lower, lowerType, upper, upperType);
        } catch (IllegalArgumentException ex) {
            String rangeString = (lowerType == BoundType.OPEN ? "(" : "[") + lower + " - " +
                    upper + (upperType == BoundType.OPEN ? ")" : "]");
            throw new InvalidRequestParamEx( rangeString, lowerName + ", " + upperName, "range",
                    "Range start must not exceed range end");
        }
    }

    protected <T extends Comparable> Range<T> getOpenRange(T lower, T upper, String lowerName, String upperName) {
        return getRange(lower, upper, lowerName, upperName, BoundType.OPEN, BoundType.OPEN);
    }

    protected <T extends Comparable> Range<T> getOpenClosedRange(T lower, T upper, String lowerName, String upperName) {
        return getRange(lower, upper, lowerName, upperName, BoundType.OPEN, BoundType.CLOSED);
    }

    protected <T extends Comparable> Range<T> getClosedOpenRange(T lower, T upper, String lowerName, String upperName) {
        return getRange(lower, upper, lowerName, upperName, BoundType.CLOSED, BoundType.OPEN);
    }

    protected <T extends Comparable> Range<T> getClosedRange(T lower, T upper, String lowerName, String upperName) {
        return getRange(lower, upper, lowerName, upperName, BoundType.CLOSED, BoundType.CLOSED);
    }

    /**
     * Throws an exception in the event of an invalid supplied enum value
     * The exception contains a listing of valid enum values
     */
    private <T extends Enum<T>> InvalidRequestParamEx getEnumParamEx(Class<T> enumType, Function<T, String> valueFunction,
                                                                     String paramName, String paramValue) {
        return new InvalidRequestParamEx(paramValue, paramName, "string",
                Arrays.asList(enumType.getEnumConstants()).stream()
                        .map(valueFunction)
                        .reduce("", (a, b) -> (StringUtils.isNotBlank(a) ? a + "|" : "") + b));
    }

    /**
     * Attempts to map the given request parameter to an enum by finding an enum instance whose name matches the parameter
     * @throws InvalidRequestParamEx if no such enum was found
     */
    protected <T extends Enum<T>> T getEnumParameter(String paramName, String paramValue, Class<T> enumType)
            throws InvalidRequestParamEx {
        T result = getEnumParameter(paramValue, enumType, null);
        if (result == null) {
            throw getEnumParamEx(enumType, Enum::toString, paramName, paramValue);
        }
        return result;
    }
    /**
     * Attempts to map the given request parameter to an enum by finding an enum instance whose name matches the parameter
     * returns a default value if no such enum was found
     */
    protected <T extends Enum<T>> T getEnumParameter(String paramValue, Class<T> enumType, T defaultValue) {
        try {
            return T.valueOf(enumType, StringUtils.upperCase(paramValue));
        } catch (IllegalArgumentException | NullPointerException ex) {
            return defaultValue;
        }
    }

    /**
     * Attempts to map the given request parameter to an enum by finding an enum using the given mapFunction
     * @throws InvalidRequestParamEx if the mapFunction returns null that lists possible values using the
     *                                  given valueFunction
     */
    protected <T extends Enum<T>> T getEnumParameterByValue(Class<T> enumType, Function<String, T> mapFunction,
                                                            Function<T, String> valueFunction,
                                                            String paramName, String paramValue) {
        T result = getEnumParameterByValue(enumType, mapFunction, paramValue, null);
        if (result == null) {
            throw getEnumParamEx(enumType, valueFunction, paramName, paramValue);
        }
        return result;
    }
    /**
     * Attempts to map the given request parameter to an enum by finding an enum using the given mapFunction
     * returns a default value if the map function returns null
     */
    protected <T extends Enum<T>> T getEnumParameterByValue(Class<T> enumType,Function<String, T> mapFunction,
                                                            String paramValue, T defaultValue) {
        T result = mapFunction.apply(paramValue);
        return result != null ? result : defaultValue;
    }

    /**
     * Returns a limit + offset extracted from the given web request parameters
     * Returns the given default limit offset if no such parameters exist
     *
     * @param webRequest WebRequest
     * @param defaultLimit int - The default limit to use, 0 for no limit
     * @return LimitOffset
     */
    protected LimitOffset getLimitOffset(WebRequest webRequest, int defaultLimit) {
        int limit = defaultLimit;
        int offset = 0;
        if (webRequest.getParameter("limit") != null) {
            String limitStr = webRequest.getParameter("limit");
            if (limitStr.equalsIgnoreCase("all")) {
                limit = 0;
            }
            else {
                limit = NumberUtils.toInt(limitStr, defaultLimit);
                if (limit > MAX_LIMIT) {
                    throw new InvalidRequestParamEx(limitStr, "limit", "int", "Must be <= " + MAX_LIMIT);
                }
            }
        }
        if (webRequest.getParameter("offset") != null) {
            offset = NumberUtils.toInt(webRequest.getParameter("offset"), 0);
        }
        return new LimitOffset(limit, offset);
    }
}
