package gov.nysenate.ess.web.service.personnel;

import gov.nysenate.ess.web.model.personnel.SupervisorOverride;
import gov.nysenate.ess.core.service.base.BaseEvent;

public class SupervisorGrantUpdateEvent extends BaseEvent
{
    private SupervisorOverride supervisorOverride;

    public SupervisorGrantUpdateEvent(SupervisorOverride supervisorOverride) {
        this.supervisorOverride = supervisorOverride;
    }

    public SupervisorOverride getSupervisorOverride() {
        return supervisorOverride;
    }
}
