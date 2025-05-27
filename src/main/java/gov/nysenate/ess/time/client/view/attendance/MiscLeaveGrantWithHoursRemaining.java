package gov.nysenate.ess.time.client.view.attendance;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.math.BigDecimal;

public class MiscLeaveGrantWithHoursRemaining implements ViewObject {
    private final MiscLeaveGrantView grant;
    private final BigDecimal hoursRemaining;

    public MiscLeaveGrantWithHoursRemaining(MiscLeaveGrantView grant, BigDecimal hoursRemaining) {
        this.grant = grant;
        this.hoursRemaining = hoursRemaining;
    }

    public MiscLeaveGrantView getGrant() {
        return grant;
    }

    public BigDecimal getHoursRemaining() {
        return hoursRemaining;
    }

    @Override
    public String getViewType() {
        return "miscleave-grant-with-hours";
    }
}
