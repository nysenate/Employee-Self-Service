package gov.nysenate.ess.core.service.cache;

import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.service.RefreshedCachedData;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.TreeMap;

/**
 * Is really a Set of current active employee IDs.
 */
public class ActiveEmployeeIdCache extends RefreshedCachedData<Integer, Object> {
    private static final Object PRESENT = new Object();
    private final EmployeeDao employeeDao;

    @Autowired
    public ActiveEmployeeIdCache(EmployeeDao employeeDao) {
        super("");
        this.employeeDao = employeeDao;
    }

    @Override
    protected Map<Integer, Object> getMap() {
        var map = new TreeMap<Integer, Object>();
        for (int empId : employeeDao.getActiveEmployeeIds()) {
            map.put(empId, PRESENT);
        }
        return map;
    }
}
