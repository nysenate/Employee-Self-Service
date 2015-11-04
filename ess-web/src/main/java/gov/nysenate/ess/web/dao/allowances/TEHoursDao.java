package gov.nysenate.ess.web.dao.allowances;

import gov.nysenate.ess.core.dao.base.BaseDao;
import gov.nysenate.ess.web.model.allowances.TEHours;

import java.util.Date;
import java.util.List;

public interface TEHoursDao extends BaseDao
{

    public List<TEHours> getTEHours(int empId, int year);

    public TEHours sumTEHours(List<TEHours> teHourses);

    public List<TEHours> getTEHours(int empId, Date beginDate, Date endDate);

}
