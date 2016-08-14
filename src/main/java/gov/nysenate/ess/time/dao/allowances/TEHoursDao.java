package gov.nysenate.ess.time.dao.allowances;

import gov.nysenate.ess.core.dao.base.BaseDao;
import gov.nysenate.ess.time.model.allowances.TEHours;

import java.util.Date;
import java.util.List;

public interface TEHoursDao extends BaseDao
{
    List<TEHours> getTEHours(int empId, int year);

    TEHours sumTEHours(List<TEHours> teHourses);

    List<TEHours> getTEHours(int empId, Date beginDate, Date endDate);
}
