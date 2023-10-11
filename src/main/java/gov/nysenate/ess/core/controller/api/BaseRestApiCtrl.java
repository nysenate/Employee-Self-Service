package gov.nysenate.ess.core.controller.api;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.authorization.permission.TravelPermissionBuilder;
import gov.nysenate.ess.travel.authorization.permission.TravelPermissionObject;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
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
    
    public static final String ADMIN_REST_PATH = REST_PATH + "admin";

    /** Maximum number of results that can be requested via the query params. */
    private static final int MAX_LIMIT = 1000;

    @Autowired private EmployeeInfoService empInfoService;
    @Autowired protected EventBus eventBus;

    @PostConstruct
    public void init() throws Exception {
        this.eventBus.register(this);
    }

    /* --- Request Parsers / Getters --- */

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
     * Check that the current subject has at least one Permission.
     * @param permissions to check
     */
    protected void checkHasPermission(Permission... permissions) {
        for (int i = 0; i < permissions.length; i++) {
            try {
                checkPermission(permissions[i]);
                break;
            }
            catch (AuthorizationException ex) {
                if (i == permissions.length - 1) {
                    throw ex;
                }
            }
        }
    }

    /**
     * Checks the currently authenticated subject has permissions to perform the given action on a travel application.
     * @param app
     * @param method
     */
    protected void checkTravelAppPermission(TravelApplication app, RequestMethod method) {
        TravelPermissionBuilder submitterPerm = new TravelPermissionBuilder()
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forEmpId(app.getSubmittedBy().getEmployeeId())
                .forAction(method);

        TravelPermissionBuilder travelerPerm = new TravelPermissionBuilder()
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forEmpId(app.getTraveler().getEmployeeId())
                .forAction(method);

        if (getSubject().isPermitted(submitterPerm.buildPermission()) || getSubject().isPermitted(travelerPerm.buildPermission())) {
            return;
        }
        else {
            throw new AuthorizationException("Unauthorized access. The user does not have the necessary permissions.");
        }
    }

    /**
     * Get the employeeId of the user making this request.
     * @return
     */
    protected int getSubjectEmployeeId() {
        SenatePerson person = (SenatePerson) getSubject().getPrincipals().getPrimaryPrincipal();
        return person.getEmployeeId();
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
        try {
            return T.valueOf(enumType, StringUtils.upperCase(paramValue));
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw getEnumParamEx(enumType, Enum::toString, paramName, paramValue);
        }
    }
    /**
     * Attempts to map the given request parameter to an enum by finding an enum instance whose name matches the parameter
     * returns a default value if null.
     */
    protected <T extends Enum<T>> T getEnumParameter(String paramName, String paramValue,
                                                     Class<T> enumType, T defaultValue) {
        if (paramValue == null) {
            return defaultValue;
        }
        return getEnumParameter(paramName, paramValue, enumType);
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

    /**
     * Parses the specified query param as a boolean or returns the default value if the param is not set.
     *
     * @param param WebRequest
     * @param defaultVal boolean
     * @return boolean
     */
    protected Boolean getBooleanParam(WebRequest request, String param, Boolean defaultVal) {
        String value = request.getParameter(param);
        try {
            return value != null
                    ? (Boolean) BooleanUtils.toBoolean(value.toLowerCase(), "true", "false")
                    : defaultVal;
        } catch (IllegalArgumentException ex) {
            throw new InvalidRequestParamEx(value, param, "boolean", "true|false");
        }
    }

    /**
     * Parse an integer from the given string value, throwing an appropriate exception if it is invalid.
     *
     * @param paramName String
     * @param paramValue String
     * @return int
     * @throws InvalidRequestParamEx if the given value is not valid.
     */
    protected int parseIntegerParam(String paramName, String paramValue) {
        try {
            return Integer.parseInt(paramValue);
        } catch (NumberFormatException ex) {
            throw new InvalidRequestParamEx(paramValue, paramName, "integer", "integer");
        }
    }

    /**
     * Extracts and parses an integer param from the given web request, throws an exception if it doesn't parse
     */
    protected int getIntegerParam(WebRequest request, String paramName) {
        String intString = request.getParameter(paramName);
        return parseIntegerParam(paramName, intString);
    }

    /**
     * An overload of getIntegerParam that returns a default int value if not present.
     * @see #getIntegerParam
     */
    protected Integer getIntegerParam(WebRequest request, String paramName, Integer defaultVal) {
        String intString = request.getParameter(paramName);
        return intString == null
                ? defaultVal
                : (Integer) parseIntegerParam(paramName, intString);
    }

    /**
     * Checks to make sure the given emp id is valid.
     *
     * @param empId int
     * @param paramName String
     * @throws InvalidRequestParamEx if the given emp doesn't exist.
     */
    protected void ensureEmpIdExists(int empId, String paramName) throws InvalidRequestParamEx {
        try {
            empInfoService.getEmployee(empId);
        } catch (EmployeeNotFoundEx ex) {
            throw new InvalidRequestParamEx(
                    Integer.toString(empId),
                    paramName,
                    "int",
                    "employee id must correspond to an employee"
            );
        }
    }

    /**
     * Checks to make sure the given emp id corresponds to an active employee.
     *
     * @param empId int
     * @param paramName String
     * @throws InvalidRequestParamEx if the given emp id is not active or doesn't exist
     */
    protected void ensureEmpIdActive(int empId, String paramName) throws InvalidRequestParamEx {
        boolean empActive;
        try {
            Employee employee = empInfoService.getEmployee(empId);
            empActive = employee.isActive();
        } catch (EmployeeNotFoundEx ex) {
            empActive = false;
        }
        if (!empActive) {
            throw new InvalidRequestParamEx(
                    Integer.toString(empId),
                    paramName,
                    "int",
                    "employee id must correspond to a currently active employee"
            );
        }
    }
}
