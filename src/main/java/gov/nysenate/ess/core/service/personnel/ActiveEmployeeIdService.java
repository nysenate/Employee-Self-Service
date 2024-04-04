package gov.nysenate.ess.core.service.personnel;

import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.service.RefreshedCachedData;
import gov.nysenate.ess.core.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Set;
import java.util.TreeMap;

/**
 * A collection of currently active employee ids.
 */
public class ActiveEmployeeIdService extends RefreshedCachedData<Integer, Object> {
    private static final Object PRESENT = new Object();

    @Autowired
    public ActiveEmployeeIdService(EmployeeDao employeeDao, @Value("${cache.cron.empid:0 0 * * * *}") String cron) {
        super(cron, () -> new TreeMap<>(CollectionUtils.keysToMap(employeeDao.getActiveEmployeeIds(), id -> PRESENT)));
    }

    public Set<Integer> getActiveEmployeeIds() {
        return dataMap().keySet();
    }

    @Override
    public CacheType cacheType() {
        return CacheType.ACTIVE_EMPLOYEE_IDS;
    }
}
