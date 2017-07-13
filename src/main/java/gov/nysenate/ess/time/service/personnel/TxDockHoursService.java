package gov.nysenate.ess.time.service.personnel;

import com.google.common.collect.Range;
import gov.nysenate.ess.time.dao.personnel.SqlDockHoursDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *  @author Brian Heitner
 *
 *  Get the Docked Hours for the given employee and date range.
 */

@Service
public class TxDockHoursService  implements DockHoursService {
    @Autowired SqlDockHoursDao sqlDockHoursDao;

    @Override
    public BigDecimal getDockHours(int empId, Range<LocalDate> dateRange) {
        return sqlDockHoursDao.getDockHourTotal(empId, dateRange);
    }
}
