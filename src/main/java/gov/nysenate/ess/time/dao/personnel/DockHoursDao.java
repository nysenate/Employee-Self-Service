package gov.nysenate.ess.time.dao.personnel;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.BaseDao;
import gov.nysenate.ess.time.model.personnel.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @author  Brian Heitner
 *
 * Data access layer for retrieving T&A supervisor info as well as setting overrides.
 */
public interface DockHoursDao extends BaseDao
{
    /**
     * @author  Brian Heitner
     *
     * Return all supervisor overrides updated since the given date time
     *
     * @param empId Integer - Employee Id
     * @param dates Range<LocalDate> - Looks for changes after this date
     * @return List<DockHoursRecord>
     */

    public List<DockHoursRecord> getDockHourRecords(Integer empId, Range<LocalDate> dates);
}