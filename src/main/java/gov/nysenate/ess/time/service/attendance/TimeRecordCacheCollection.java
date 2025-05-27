package gov.nysenate.ess.time.service.attendance;

import gov.nysenate.ess.time.model.attendance.TimeRecord;

import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/** Helper class to store a collection of time records in a cache. */
class TimeRecordCacheCollection {
    private final Map<BigInteger, TimeRecord> cachedTimeRecords = new LinkedHashMap<>();

    public TimeRecordCacheCollection(Collection<TimeRecord> cachedTimeRecords) {
        cachedTimeRecords.forEach(this::update);
    }

    public List<TimeRecord> getTimeRecords() {
        // Return a copy of each time record
        return cachedTimeRecords.values().stream()
                .map(TimeRecord::new)
                .collect(toList());
    }

    public void update(TimeRecord record) {
        if (record.getTimeRecordId() == null) {
            throw new IllegalArgumentException("Attempt to insert time record with null id into cache");
        }
        cachedTimeRecords.put(record.getTimeRecordId(), record);
    }

    public void remove(BigInteger timeRecId) {
        cachedTimeRecords.remove(timeRecId);
    }
}
