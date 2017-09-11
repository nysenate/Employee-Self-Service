package gov.nysenate.ess.time.service.personnel;

import com.google.common.collect.Range;
import gov.nysenate.ess.time.dao.personnel.DockHoursDao;
import gov.nysenate.ess.time.model.personnel.DockHoursRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 *  @author Brian Heitner
 *
 *  Get the Docked Hours for the given employee and date range.
 */
@Service
public class TxDockHoursService  implements DockHoursService {

    @Autowired private DockHoursDao dockHoursDao;

    /**
     * Return the Total Docked Hours within a specified date range. Since docked hours
     * are summed up for each record based on the SQL Query used in getDockHourRecords,
     * we only need to look at the latest record. This record can be earlier than the
     * end date because SFMS may not have processed records up to the end date. Docked
     * Hours will only  be in processed records.
     *
     * @param empId Integer - Employee Id
     * @param dateRange Range<LocalDate> - Looks for changes after this date
     * @return BigDecimal  - Total Docked Hours
     */
    @Override
    public BigDecimal getDockHours(int empId, Range<LocalDate> dateRange) {

        Comparator<DockHoursRecord> dockHoursRecordDesc =
                Comparator.comparing(DockHoursRecord::getEndDate).reversed();

        List<DockHoursRecord> docHoursRecords = dockHoursDao.getDockHourRecords(empId, dateRange);

        Optional<DockHoursRecord> latestRec = docHoursRecords.stream()
                .sorted(dockHoursRecordDesc)
                .findFirst();

        return latestRec
                .map(DockHoursRecord::getDockHours)
                .orElse(BigDecimal.ZERO);
    }
}
