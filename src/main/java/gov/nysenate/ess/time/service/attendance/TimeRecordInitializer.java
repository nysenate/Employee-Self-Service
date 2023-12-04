package gov.nysenate.ess.time.service.attendance;

import gov.nysenate.ess.time.model.attendance.TimeRecord;

public interface TimeRecordInitializer {
    void initializeEntries(TimeRecord timeRecord);
}
