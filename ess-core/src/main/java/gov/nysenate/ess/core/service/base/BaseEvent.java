package gov.nysenate.ess.core.service.base;

import java.time.LocalDateTime;

public class BaseEvent
{
    protected LocalDateTime createdDateTime;

    public BaseEvent() {
        this.createdDateTime = LocalDateTime.now();
    }

    public BaseEvent(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }
}
