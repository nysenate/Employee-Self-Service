package gov.nysenate.ess.time.service.personnel;

import gov.nysenate.ess.core.service.base.BaseEvent;
import gov.nysenate.ess.time.model.personnel.SupervisorOverride;

public class SupervisorGrantUpdateEvent extends BaseEvent {
    private final SupervisorOverride supervisorOverride;

    public SupervisorGrantUpdateEvent(SupervisorOverride supervisorOverride) {
        this.supervisorOverride = supervisorOverride;
    }

    public SupervisorOverride getSupervisorOverride() {
        return supervisorOverride;
    }
}
