package gov.nysenate.ess.core.model.auth;

import com.google.common.collect.Range;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a wildcard permission that is only valid for a period of time
 * @see WildcardPermission
 * DateTimeRangePermission cannot imply a non DateTimeRangePermission unless it includes all
 */
public class DateTimeRangePermission extends WildcardPermission {

    /** The date time range that this permission is effective for */
    private Range<LocalDateTime> effectiveRange;

    public DateTimeRangePermission(String wildcardString, Range<LocalDateTime> effectiveRange) {
        super(wildcardString);
        this.effectiveRange = effectiveRange;
    }

    /** An overload that is effective only for a specific date time */
    public DateTimeRangePermission(String wildcardString, LocalDateTime effectiveDateTime) {
        this(wildcardString, Range.singleton(effectiveDateTime));
    }

    /** Overload that is effective over a specific date */
    public DateTimeRangePermission(String wildcardString, LocalDate effectiveDate) {
        this(wildcardString, Range.closedOpen(
                effectiveDate.atStartOfDay(), effectiveDate.plusDays(1).atStartOfDay()));
    }

    /**
     * Return true if this permission's wildcard permission component implies the other's
     *  and the effective date time range of this permission encloses the other's
     * @see WildcardPermission#implies(Permission)
     * @param p Permission
     * @return boolean
     */
    @Override
    public boolean implies(Permission p) {
        // Unless the effective range includes all dates,
        // A date time range permission cannot imply a standard permission
        if (!(p instanceof DateTimeRangePermission)) {
            return effectiveRange.encloses(Range.all()) && super.implies(p);
        }

        return super.implies(p) && this.effectiveRange.encloses(((DateTimeRangePermission) p).effectiveRange);
    }
}
