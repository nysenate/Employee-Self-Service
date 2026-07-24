package gov.nysenate.ess.time.client.view.attendance;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.model.payroll.MiscLeaveType;

/**
 * Exposes the display labels of a {@link MiscLeaveType} to the front end.
 *
 * These labels were historically only available to JSP pages, which receive them as the
 * "miscLeaves" request attribute set by CommonAttributeFilter. The React front end reads
 * them from the /miscleave/types endpoint instead.
 */
public class MiscLeaveTypeView implements ViewObject {

    private final String type;
    private final String code;
    private final String shortName;
    private final String fullName;
    private final boolean restricted;

    public MiscLeaveTypeView(MiscLeaveType miscLeaveType) {
        this.type = miscLeaveType.name();
        this.code = miscLeaveType.getCode();
        this.shortName = miscLeaveType.getShortName();
        this.fullName = miscLeaveType.getFullName();
        this.restricted = miscLeaveType.isRestricted();
    }

    public String getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean isRestricted() {
        return restricted;
    }

    @Override
    public String getViewType() {
        return "misc-leave-type";
    }
}
