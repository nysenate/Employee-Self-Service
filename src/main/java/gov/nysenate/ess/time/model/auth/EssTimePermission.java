package gov.nysenate.ess.time.model.auth;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.auth.DateTimeRangePermission;
import gov.nysenate.ess.core.util.DateUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Extension of {@link DateTimeRangePermission} that constructs permissions in a standardized format
 * The format is as follows:
 *      time:employee-{employee id}:{affected object}:{rest method}
 * This roughly translates to the permission to execute {rest method} on the {affected object},
 * which belongs to the employee with an id of {employee id}
 */
public class EssTimePermission extends DateTimeRangePermission {

    private static final String TIME_DOMAIN = "time";
    private static final String EMP_ID_PART_PREFIX = "employee";

    /**
     * Construct a permission that grants all actions on all ess-time objects under the given employee
     * for all time:
     *      time:employee-{employee id}
     * Should be granted to the employee
     * @param empId int employee id
     */
    public EssTimePermission(int empId) {
        super(getEmployeePart(empId), Range.all());
    }

    /**
     * Construct a permission that grants an actions to an ess-time object under the given employee
     *  for the given date range
     * @param empId int employee id
     * @param effectiveRange Range<LocalDate> effective dates
     */
    public EssTimePermission(int empId, TimePermissionObject object, RequestMethod action,
                             Range<LocalDate> effectiveRange) {
        super(getPermissionString(empId, object, action), DateUtils.toDateTimeRange(effectiveRange));
    }

    /**
     * Construct a permission that grants an actions to an ess-time object under the given employee
     * for only the given date time
     * @param empId int employee id
     * @param effectiveDateTime LocalDateTime
     */
    public EssTimePermission(int empId, TimePermissionObject object, RequestMethod action,
                             LocalDateTime effectiveDateTime) {
        super(getPermissionString(empId, object, action), effectiveDateTime);
    }

    /**
     * Construct a permission that grants an actions to an ess-time object under the given employee
     * for only the given date
     * @param empId int employee id
     * @param effectiveDate LocalDate
     */
    public EssTimePermission(int empId, TimePermissionObject object, RequestMethod action, LocalDate effectiveDate) {
        super(getPermissionString(empId, object, action), effectiveDate);
    }

    /** Construct a permission that grants an action on an object for all employees for all time */
    public EssTimePermission(TimePermissionObject object, RequestMethod action) {
        super(getPermissionString(null, object, action), Range.all());
    }

    /** --- Internal Methods --- */

    private static String getEmployeePart(Integer empId) {
        return TIME_DOMAIN + PART_DIVIDER_TOKEN
                + EMP_ID_PART_PREFIX + SUBPART_DIVIDER_TOKEN +
                (empId != null ? empId : "*");
    }

    private static String getPermissionString(Integer empId, TimePermissionObject object, RequestMethod action) {
        return getEmployeePart(empId) + PART_DIVIDER_TOKEN +
                object + PART_DIVIDER_TOKEN +
                action;
    }
}
