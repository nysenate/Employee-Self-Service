package gov.nysenate.ess.seta.service.personnel;

import gov.nysenate.ess.seta.model.personnel.SupervisorOverride;
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
