package gov.nysenate.ess.core.model.auth;

import com.google.common.base.Objects;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.RangeUtils;
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
    private final Range<LocalDateTime> effectiveRange;

    /**
     * When false, {@link DateTimeRangePermission}s with date ranges that intersect with this permission's range
     * (and a matching wildcard) will imply this permission.
     * When true, {@link DateTimeRangePermission}s must have a date range
     * that completely encloses this permission's range in order to imply this permission.
     */
    private final boolean requireEnclosing;

    public DateTimeRangePermission(String wildcardString, Range<LocalDateTime> effectiveRange, boolean requireEnclosing) {
        super(wildcardString);
        this.effectiveRange = effectiveRange;
        this.requireEnclosing = requireEnclosing;
    }

    /** An overload that does not accept overlapping dates */
    public DateTimeRangePermission(String wildcardString, Range<LocalDateTime> effectiveRange) {
        this(wildcardString, effectiveRange, true);
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
     *  and the effective date time range of this permission encloses or intersects the other's,
     *  depending on the other permission's requireEnclosing setting.
     * @see WildcardPermission#implies(Permission)
     * @param p Permission
     * @return boolean
     */
    @Override
    public boolean implies(Permission p) {
        // First check wildcard permission
        if (!super.implies(p)) {
            return false;
        }
        // Unless the effective range includes all dates,
        // A date time range permission cannot imply a standard permission
        if (!(p instanceof DateTimeRangePermission)) {
            return effectiveRange.encloses(Range.all());
        }

        DateTimeRangePermission dtp = (DateTimeRangePermission) p;

        if (dtp.requireEnclosing) {
            return this.effectiveRange.encloses(dtp.effectiveRange);
        }
        return RangeUtils.intersects(this.effectiveRange, dtp.effectiveRange);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateTimeRangePermission)) return false;
        if (!super.equals(o)) return false;
        DateTimeRangePermission that = (DateTimeRangePermission) o;
        return Objects.equal(effectiveRange, that.effectiveRange);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), effectiveRange);
    }
}
