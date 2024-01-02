package gov.nysenate.ess.core.service.cache;

import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.service.RefreshedCachedData;
import gov.nysenate.ess.core.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.TreeMap;

/**
 * Is really a Set of current active employee IDs.
 */
public class ActiveEmployeeIdCache extends RefreshedCachedData<Integer, Object> {
    private static final Object PRESENT = new Object();

    @Autowired
    public ActiveEmployeeIdCache(EmployeeDao employeeDao) {
        super(() -> new TreeMap<>(CollectionUtils.keysToMap(employeeDao.getActiveEmployeeIds(), id -> PRESENT)));
    }
}
